package com.goosun.apiconsumer.service;


public interface FileStroeService {

    String uploadFile(String objectName, String filePath);

    String uploadString(String objectName, String content);

    void downloadFile(String objectName, String filePath);

    boolean existsFile(String objectName);

    String getUrl(String objectName);

    void deleteFolder(String objectName);

}
