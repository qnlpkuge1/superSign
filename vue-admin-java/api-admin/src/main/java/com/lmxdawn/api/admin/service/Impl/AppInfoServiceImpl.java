package com.lmxdawn.api.admin.service.Impl;

import ch.qos.logback.core.util.FileUtil;
import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.AppInfoDao;
import com.lmxdawn.api.admin.dto.SignProcess;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.exception.BizException;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.service.*;
import com.lmxdawn.api.admin.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Value("${app.createProfileScript}")
    private String createProfileScript;

    @Value("${app.appServer}")
    private String appServer;

    @Value("${app.ipa-storage}")
    private String ipaStorage;

    private static final long PROCESS_EXPIRE = 100;

    @Autowired
    private AppInfoDao appInfoDao;

    @Autowired
    private FileStroeService fileStroeService;

    @Autowired
    private UDIDService udidService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SslProperties sslProperties;

    @Autowired
    private JobService jobService;

    @Override
    public AppInfo add(String ipaFilePath) {
        File file = new File(ipaFilePath);
        if (!file.exists()) {
            return new AppInfo();
        }
        Map<String, String> meta = IPAUtils.readIPA(ipaFilePath);
        AppInfo appInfo = new AppInfo(meta);
        appInfo.setOwnerId(ThreadVariable.getUserId());
        appInfoDao.insert(appInfo);

        appInfo.setIconPath(translateIcon(appInfo, file.getParent()));
        appInfo.setFullIconPath(translateFullIcon(appInfo, file.getParent()));
        appInfo.setPath(moveIpa(appInfo, ipaFilePath));
        appInfo.setQrcodePath(getQrcodePath(appInfo, file.getParent()));
        appInfo.setMbConfig(genMbconfig(appInfo));
        appInfoDao.updateIpa(appInfo);
        return appInfo;
    }

    private String getQrcodePath(AppInfo appInfo, String parentPath) {
        String url = getUrl(appInfo.getId());
        String filePath = QRCodeUtil.SaveQrCode(url, "png", 231, 231, parentPath, "qrcode");
        String objectName = String.format("app/qrcode/%s.png", appInfo.getId());
        return fileStroeService.uploadFile(objectName, filePath);
    }

    @Override
    public AppInfo update(AppInfo appInfo, String ipaFilePath) {
        File file = new File(ipaFilePath);
        if (!file.exists()) {
            return new AppInfo();
        }
        Map<String, String> meta = IPAUtils.readIPA(ipaFilePath);
        appInfo.setVersion(meta.get("version"));
        appInfo.setVersionCode(meta.get("version"));
        appInfo.setAppName(meta.get("appName"));

        appInfo.setIconPath(translateIcon(appInfo, file.getParent()));
        appInfo.setFullIconPath(translateFullIcon(appInfo, file.getParent()));
        moveIpa(appInfo, ipaFilePath);
        appInfoDao.updateIpa(appInfo);

        return appInfo;
    }

    private String genMbconfig(AppInfo appInfo) {
        String objectName = String.format("app/%s/udid.mobileconfig", appInfo.getId());
        String installUrl = String.format("%s/udid/%s/install", appServer, appInfo.getId());
        String mbconfig = String.format(udidService.getMbconfigTemp(1), installUrl,
                appInfo.getAppName(), UUID.randomUUID().toString().toUpperCase());
        String sourcePath = writeFile(appInfo, mbconfig);
        String signedPath = signMbconfig(sourcePath);
        return fileStroeService.uploadFile(objectName, signedPath);
    }

    private String signMbconfig(String sourcePath) {
        File source = new File(sourcePath);
        if (!source.exists()) {
            throw new BizException(12204, ".mobileconfig文件不存在，无法签名");
        }
        String targetPath = source.getParent() + File.separator + "signed.mobileconfig";
        String cmd = String.format("openssl smime -sign -in %s -out %s -signer %s -inkey %s -certfile %s -outform der -nodetach",
                sourcePath, targetPath, sslProperties.getMbcrt(), sslProperties.getMbkey(), sslProperties.getCacrt());
        this.exeCmd(cmd);
        return targetPath;
    }

    private String writeFile(AppInfo appInfo, String mbconfigStr) {
        String mbconfigPath = String.format("%sapp/%s/udid.mobileconfig", sourcePath, appInfo.getId());
        try (FileWriter writer = new FileWriter(mbconfigPath);) {
            writer.write(mbconfigStr);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mbconfigPath;
    }

    @Override
    public AppInfo setInstallLimit(Integer id, Integer installLimit) {
        appInfoDao.updateInstallLimit(id, installLimit);
        return appInfoDao.get(id);
    }

    @Override
    public List<AppInfo> list(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        request.setOwnerId(ThreadVariable.getUserId());
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return appInfoDao.list(request);
    }

    @Override
    public List<AppInfo> allList(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return appInfoDao.list(request);
    }

    @Override
    public AppInfo getAppInfo(Integer id) {
        return appInfoDao.get(id);
    }

    @Override
    @Async
    public void sendJob(AppInfo appInfo, Equipment equipment, Developer developer) {
        String key = getSignProcessKey(appInfo.getId(), equipment);

        redisService.set(key, new SignProcess(3), PROCESS_EXPIRE);

        updateMobileProvision(appInfo, equipment, developer);
        redisService.set(key, new SignProcess(38), PROCESS_EXPIRE);

        jobService.sendJob(new Job(appInfo, equipment, developer));
    }

    /**
     * 更新.mobileprovision文件到oss
     *
     * @param appInfo
     * @param equipment
     * @param developer
     */
    private void updateMobileProvision(AppInfo appInfo, Equipment equipment, Developer developer) {
        String parentPath = distPath + appInfo.getId() + File.separator + equipment.getUdid();
        String profile = parentPath + File.separator + "dist.mobileprovision";
        File profileFile = new File(profile);

        String objectName = getMobileProvisionObjectName(appInfo, equipment);

        if (!fileStroeService.existsFile(objectName)) {
            logger.info("对象文件不存在：" + objectName);
            if (!profileFile.exists()) {
                FileUtil.createMissingParentDirectories(profileFile);
                String commandStr = String.format("ruby %s %s %s %s %s %s %s %s ", resignScript, developer.getUsername(),
                        developer.getPassword(), appInfo.getAppId(), appInfo.getAppName(), equipment.getUdid(), equipment.getProduct(), parentPath);
                logger.info(commandStr);
                execCmd(commandStr, null);
            }
            fileStroeService.uploadFile(objectName, profile);
        }
    }

    private String getMobileProvisionObjectName(AppInfo appInfo, Equipment equipment) {
        return String.format("dist/%s/%s/dist.mobileprovision", appInfo.getId(), equipment.getUdid());
    }


    private String getSignProcessKey(Integer id, Equipment equipment) {
        return String.format("signProcess::%s-%s", id, equipment.getUdid());
    }

    /**
     * 是否已经在重签名队列
     *
     * @param equipment
     * @return
     */
    @Override
    public boolean inTranscocdQueue(Integer id, Equipment equipment) {
        String key = getSignProcessKey(id, equipment);
        long expire = redisService.getExpire(key);
        if (expire >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean enableAppInfo(Integer id) {
        return appInfoDao.enableAppInfo(id);
    }

    @Override
    public boolean disableAppInfo(Integer id) {
        return appInfoDao.disableAppInfo(id);
    }

    @Override
    public String getUrl(Integer id) {
        return String.format("%s/app/%s", appServer, id);
    }

    @Override
    @Async
    public void initProfile(Integer appInfoId, List<Developer> developers) {

        AppInfo appInfo = this.getAppInfo(appInfoId);
        for (Developer developer : developers) {
            String cmd = String.format("ruby %s %s %s %s %s", createProfileScript, developer.getUsername(),
                    developer.getPassword(), appInfo.getAppId(), UUID.randomUUID());
            logger.info(cmd);
            execCmd(cmd, null);
        }

    }

    @Override
    @Cacheable(value = "signProcess", key = "#p0+'-'+#p1")
    public SignProcess getSignProcess(Integer id, String udid) {
        return new SignProcess();
    }

    @Override
    @Async
    public void resign(AppInfo appInfo, Equipment equipment, Developer developer) {
        String key = getSignProcessKey(appInfo.getId(), equipment);
        String parentPath = distPath + appInfo.getId() + File.separator + equipment.getUdid();
        String targetPath = parentPath + File.separator + "dist.ipa";
        File targetDir = new File(targetPath);
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

        redisService.set(key, new SignProcess(68), PROCESS_EXPIRE);
        sign(targetPath, profile, developer.getCert());

        String ipaFileUrl = getIpaStorage(appInfo, equipment, targetPath);
        redisService.set(key, new SignProcess(90), PROCESS_EXPIRE);

        String plistObjectName = String.format("dist/%s/%s/dist.plist", appInfo.getId(), equipment.getUdid());
        String plist = genPlist(udidService.getPlistTemp(1), appInfo, ipaFileUrl);
        String plistUrl = fileStroeService.uploadString(plistObjectName, plist);
        redisService.set(key, new SignProcess(100, "successed", plistUrl), PROCESS_EXPIRE);
    }

    private String getIpaStorage(AppInfo appInfo, Equipment equipment, String targetPath) {
        if (ipaStorage.equals("local")) {
            return String.format("%s/%s/%s/dist.ipa", downloadServer, appInfo.getId(), equipment.getUdid());
        }

        String objectName = String.format("dist/%s/%s/dist.ipa", appInfo.getId(), equipment.getUdid());
        return fileStroeService.uploadFile(objectName, targetPath);
    }

    @Override
    public void allocatTicket(AppInfo appInfo, List<Integer> tickets) {
        redisService.rpushAll(getAppTicketKey(appInfo), tickets);
    }

    private String getAppTicketKey(AppInfo appInfo) {
        return String.format("app:ticket::%s", appInfo.getId());
    }

    //消费安装票据
    @Override
    public Integer consumeTicket(AppInfo appInfo, Equipment equipment) {

        Object obj = redisService.lpop(getAppTicketKey(appInfo));
        if (obj != null) {
            Integer developerId = Integer.parseInt(obj.toString());

            return developerId;
        }
        return null;
    }

    @Override
    public long getAppTicketLen(AppInfo appInfo) {
        return redisService.lGetListSize(getAppTicketKey(appInfo));
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

    private void execCmd(String cmd, String args[]) {
        try {
            Process prc = Runtime.getRuntime().exec(cmd, args);
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

    private String translateIcon(AppInfo appInfo, String parentPath) {
        String objectName = String.format("app/%s/icon.png", appInfo.getId());
        String iconPath = parentPath + File.separator + "icon.png";
        String iconFixPath = parentPath + File.separator + "fixIcon.png";
        File file = new File(iconFixPath);
        if (file.exists()) {
            file.delete();
        }
        String commandStr = String.format("pngdefry -i %s -o %s", iconPath, iconFixPath);
        exeCmd(commandStr);
        if (!file.exists()) {
            iconFixPath = iconPath;
        }

        return fileStroeService.uploadFile(objectName, iconFixPath);
    }

    private String translateFullIcon(AppInfo appInfo, String parentPath) {
        String objectName = String.format("app/%s/fullIcon.png", appInfo.getId());
        String iconPath = parentPath + File.separator + "fullIcon.png";
        String iconFixPath = parentPath + File.separator + "fixFullIcon.png";
        File file = new File(iconFixPath);
        if (file.exists()) {
            file.delete();
        }
        String commandStr = String.format("pngdefry -i %s -o %s", iconPath, iconFixPath);
        exeCmd(commandStr);
        //存在不需要转换的图片格式；
        if (!file.exists()) {
            iconFixPath = iconPath;
        }
        return fileStroeService.uploadFile(objectName, iconFixPath);
    }

    private String moveIpa(AppInfo appInfo, String ipaFilePath) {
        String ipaTargetPath = String.format("app/%s/dist.ipa", appInfo.getId());
        appInfo.setPath(ipaTargetPath);
        File targetDir = new File(sourcePath + ipaTargetPath);
        if (!targetDir.exists()) {
            FileUtil.createMissingParentDirectories(targetDir);
        }
        fileStroeService.uploadIpaAsync(appInfo, ipaTargetPath, ipaFilePath);
        return ipaTargetPath;
    }

    private void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            logger.info(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
