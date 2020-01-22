package com.lmxdawn.api.admin.entity.app;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class AppInfo implements Serializable {

    private Integer id;

    private String appId;

    private String appIdReal;

    private String appName;

    private String version;

    private String versionCode;

    private String path;

    private String iconPath;

    private String fullIconPath;

    private Integer installLimit;

    private Integer status;

    private Date createTime;

    private String mbConfig;

    private Long ownerId;

    private String qrcodePath;

    public AppInfo() {
    }

    public AppInfo(Map<String, String> meta) {
        this.appIdReal = meta.get("package");
        this.appId = appIdReal.substring(0, appIdReal.lastIndexOf('.') + 1) + "*";
        this.appName = meta.get("appName");
        this.version = meta.get("version");
        this.versionCode = meta.get("version");
        this.createTime = new Date();
        this.status = -1;
        this.installLimit = 0;
    }

}
