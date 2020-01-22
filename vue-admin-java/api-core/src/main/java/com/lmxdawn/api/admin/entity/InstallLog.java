package com.lmxdawn.api.admin.entity;

import com.lmxdawn.api.admin.entity.app.AppInfo;
import lombok.Data;

import java.util.Date;

@Data
public class InstallLog {

    private Integer id;

    private String product;

    private String udid;

    private Integer appInfoId;

    private String appName;

    private Integer developerId;

    private String developerName;

    private Date createTime;

    private Long ownerId;

    //扣费装机量
    private Integer deduction = 0;

    public InstallLog() {
    }

    public InstallLog(AppInfo appInfo, Equipment equipment, Developer developer) {
        this.ownerId = appInfo.getOwnerId();
        this.appInfoId = appInfo.getId();
        this.appName = appInfo.getAppName();
        this.udid = equipment.getUdid();
        this.product = equipment.getProduct();
        this.developerId = developer.getId();
        this.developerName = developer.getUsername();
    }

}
