package com.lmxdawn.api.admin.entity;

import com.lmxdawn.api.admin.entity.app.AppInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class Job implements Serializable {
    private String type = "resign";
    private AppInfo appInfo;
    private Equipment equipment;
    private Developer developer;

    public Job(AppInfo appInfo, Equipment equipment, Developer developer) {
        this.appInfo = appInfo;
        this.equipment = equipment;
        this.developer = developer;
    }

    public Job(String type, AppInfo appInfo) {
        this.type = type;
        this.appInfo = appInfo;
    }
}
