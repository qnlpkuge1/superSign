package com.lmxdawn.api.admin.service.Impl;

import ch.qos.logback.core.util.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.service.FileStroeService;
import com.lmxdawn.api.admin.service.JobService;
import com.lmxdawn.api.admin.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;

@Service
public class FileStoreServiceImpl implements FileStroeService {

    @Value("${app.objStore.accKey}")
    private String accKey;

    @Value("${app.objStore.secKey}")
    private String secKey;

    @Value("${app.objStore.endPoint}")
    private String endPoint;

    @Value("${app.objStore.bucket}")
    private String bucket;

    @Value("${app.objStore.domain}")
    private String domain;

    @Autowired
    private JobService jobService;

    @Override
    public String uploadFile(String objectName, String filePath) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ossClient.putObject(bucket, objectName, new File(filePath));
        ossClient.shutdown();
        return domain + objectName;
    }

    @Override
    @Async
    public String uploadIpaAsync(AppInfo appInfo, String objectName, String filePath) {
        String url = this.uploadFile(objectName, filePath);
        jobService.sendJob(new Job(Constant.DOWNLOAD_STRATEGY, appInfo));
        return url;
    }

    @Override
    public String uploadString(String objectName, String content) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ossClient.putObject(bucket, objectName, new ByteArrayInputStream(content.getBytes()));
        ossClient.shutdown();
        return domain + objectName;
    }

    @Override
    public void downloadFile(String objectName, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            FileUtil.createMissingParentDirectories(file);
        }

        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ossClient.getObject(new GetObjectRequest(bucket, objectName), new File(filePath));
        ossClient.shutdown();
    }


    @Override
    public boolean existsFile(String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        boolean found = ossClient.doesObjectExist(bucket, objectName);
        ossClient.shutdown();
        return found;
    }
}
