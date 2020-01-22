package com.lmxdawn.api.admin.service;


import com.lmxdawn.api.admin.entity.app.AppInfo;

public interface FileStroeService {

    String uploadFile(String objectName, String filePath);

    String uploadIpaAsync(AppInfo appInfo, String objectName, String filePath);

    String uploadString(String objectName, String content);

    void downloadFile(String objectName, String filePath);

    boolean existsFile(String objectName);
}
