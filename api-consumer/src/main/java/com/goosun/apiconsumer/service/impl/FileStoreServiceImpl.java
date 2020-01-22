package com.goosun.apiconsumer.service.impl;

import ch.qos.logback.core.util.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.goosun.apiconsumer.service.FileStroeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String uploadFile(String objectName, String filePath) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ossClient.putObject(bucket, objectName, new File(filePath));
        ossClient.shutdown();
        return domain + objectName;
    }

    @Override
    public String uploadString(String objectName, String content) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ossClient.putObject(bucket, objectName, new ByteArrayInputStream(content.getBytes()));
        ossClient.shutdown();
        return domain + objectName;
    }

    @Override
    @Async
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

    @Override
    public String getUrl(String objectName) {
        return domain + objectName;
    }

    @Override
    public void deleteFolder(String folderName) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accKey, secKey);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket);
        listObjectsRequest.setPrefix(folderName);
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);

        if(listing.getObjectSummaries().isEmpty()){
            return ;
        }

        List<String> keys = new ArrayList<String>();
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
            keys.add(objectSummary.getKey());
        }
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(keys));
        ossClient.shutdown();
    }
}
