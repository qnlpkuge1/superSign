package com.goosun.apiconsumer.service.chain.impl;

import com.goosun.apiconsumer.service.FileStroeService;
import com.goosun.apiconsumer.service.chain.JobProcessChain;
import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.util.Constant;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class JobProcessDownloadHandle extends JobProcessChain {

    @Autowired
    private FileStroeService fileStroeService;

    @Value("${app.sourcePath}")
    private String sourcePath;

    @Value("${app.distPath}")
    private String distPath;

    @Override
    public void doProcess(Job job) {
        if (!Constant.DOWNLOAD_STRATEGY.equals(job.getType()) || job.getAppInfo() == null) {
            return;
        }

        AppInfo appInfo = job.getAppInfo();

        System.out.println("执行IPA下载任务");
        fileStroeService.downloadFile(appInfo.getPath(), sourcePath + appInfo.getPath());

        //清理已签名缓存的IPA文件
        File distDir = new File(distPath + appInfo.getId());
        if (distDir.exists()) {
            try {
                System.out.println("正在清空目录:" + distDir.getPath());
                FileUtils.cleanDirectory(distDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String objectName = String.format("dist/ipa/%s/",appInfo.getId());
        fileStroeService.deleteFolder(objectName);
    }
}
