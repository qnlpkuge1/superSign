package com.goosun.apiconsumer.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.goosun.apiconsumer.service.AppInfoService;
import com.goosun.apiconsumer.service.FileStroeService;
import com.goosun.apiconsumer.service.UDIDService;
import com.lmxdawn.api.admin.dto.SignProcess;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.util.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StopWatch;

import java.io.*;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    private Logger logger = LoggerFactory.getLogger(AppInfoServiceImpl.class);

    @Value("${app.distPath}")
    private String distPath;

    @Value("${app.sourcePath}")
    private String sourcePath;

    @Value("${app.downloadServer}")
    private String downloadServer;

    @Value("${app.resignScript}")
    private String resignScript;

    @Value("${app.ipa-storage}")
    private String ipaStorage;

    private static final long PROCESS_EXPIRE = 100;

    @Autowired
    private FileStroeService fileStroeService;

    @Autowired
    private UDIDService udidService;

    @Autowired
    private RedisService redisService;

    private String getSignProcessKey(Integer id, Equipment equipment) {
        return String.format("signProcess::%s-%s", id, equipment.getUdid());
    }

    private String getMobileProvisionObjectName(AppInfo appInfo, Equipment equipment) {
        return String.format("dist/%s/%s/dist.mobileprovision", appInfo.getId(), equipment.getUdid());
    }

    @Override
    @Async
    public void resign(AppInfo appInfo, Equipment equipment, Developer developer) {
        StopWatch stopWatch = new StopWatch();
        String key = getSignProcessKey(appInfo.getId(), equipment);
        String parentPath = distPath + appInfo.getId() + File.separator + equipment.getUdid();
        String targetPath = parentPath + File.separator + "dist.ipa";
        File targetDir = new File(targetPath);
        String ipaFileUrl = "";

        if (hasCachedIpa(appInfo, equipment)) {
            ipaFileUrl = fileStroeService.getUrl(getIpaObjectName(appInfo, equipment));
        } else {
            try {
                if (!targetDir.exists()) {
                    FileUtil.createMissingParentDirectories(targetDir);
                    FileCopyUtils.copy(new File(sourcePath + appInfo.getPath()), targetDir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String profile = parentPath + File.separator + "dist.mobileprovision";
            File profileFile = new File(profile);
            redisService.set(key, new SignProcess(47), PROCESS_EXPIRE);
            if (!profileFile.exists()) {
                String objectName = getMobileProvisionObjectName(appInfo, equipment);
                fileStroeService.downloadFile(objectName, profile);
            }
            stopWatch.start("sign");
            redisService.set(key, new SignProcess(58), PROCESS_EXPIRE);
            sign(targetPath, profile, developer.getCert());
            stopWatch.stop();

            redisService.set(key, new SignProcess(78), PROCESS_EXPIRE);
            stopWatch.start("ipa upload");
            ipaFileUrl = getIpaStorage(appInfo, equipment, targetPath);
            stopWatch.stop();
        }

        String plistObjectName = String.format("dist/%s/%s/dist.plist", appInfo.getId(), equipment.getUdid());
        String plist = genPlist(udidService.getPlistTemp(1), appInfo, ipaFileUrl);
        String plistUrl = fileStroeService.uploadString(plistObjectName, plist);
        redisService.set(key, new SignProcess(100, "successed", plistUrl), PROCESS_EXPIRE);

        logger.info(stopWatch.prettyPrint());
    }

    private String getIpaStorage(AppInfo appInfo, Equipment equipment, String targetPath) {
        if (ipaStorage.equals("local")) {
            return String.format("%s/%s/%s/dist.ipa", downloadServer, appInfo.getId(), equipment.getUdid());
        }

        String objectName = getIpaObjectName(appInfo, equipment);
        return fileStroeService.uploadFile(objectName, targetPath);
    }

    private boolean hasCachedIpa(AppInfo appInfo, Equipment equipment) {
        String objectName = getIpaObjectName(appInfo, equipment);
        return fileStroeService.existsFile(objectName);
    }

    private String getIpaObjectName(AppInfo appInfo, Equipment equipment) {
        return String.format("dist/ipa/%s/%s/dist.ipa", appInfo.getId(), equipment.getUdid());
    }

    private String genPlist(String plist, AppInfo appInfo, String ipaUrl) {
        return String.format(plist, ipaUrl, appInfo.getIconPath(), appInfo.getIconPath(), appInfo.getAppIdReal(),
                appInfo.getVersion(), appInfo.getAppName(), appInfo.getAppName());
    }

    private void sign(String targetPath, String profile, String certId) {
        String cmds[] = new String[]{"fastlane", "sigh", "resign", targetPath, "--signing_identity", certId, "-p", profile};
        try {
            Process prc = Runtime.getRuntime().exec(cmds);
            StreamGobbler errorGobbler = new StreamGobbler(prc.getErrorStream(), "Error");
            StreamGobbler outputGobbler = new StreamGobbler(prc.getInputStream(), "Output");
            errorGobbler.start();
            outputGobbler.start();
            prc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

}
