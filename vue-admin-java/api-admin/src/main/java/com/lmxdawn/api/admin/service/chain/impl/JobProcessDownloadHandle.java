package com.lmxdawn.api.admin.service.chain.impl;

import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.service.FileStroeService;
import com.lmxdawn.api.admin.service.chain.JobProcessChain;
import com.lmxdawn.api.admin.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JobProcessDownloadHandle extends JobProcessChain {

    @Autowired
    private FileStroeService fileStroeService;

    @Value("${app.sourcePath}")
    private String sourcePath;

    @Override
    public void doProcess(Job job) {
        if (!Constant.DOWNLOAD_STRATEGY.equals(job.getType()) || job.getAppInfo() == null) {
            return;
        }
        fileStroeService.downloadFile(job.getAppInfo().getPath(), sourcePath + job.getAppInfo().getPath());
    }
}
