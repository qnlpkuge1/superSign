package com.lmxdawn.api.admin.service;


import com.lmxdawn.api.admin.dto.SignProcess;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;

import java.util.List;

public interface AppInfoService {

    AppInfo add(String ipaFilePath);

    AppInfo update(AppInfo appInfo, String ipaFilePath);

    AppInfo setInstallLimit(Integer id, Integer installLimit);

    List<AppInfo> list(AppInfoQueryRequest request);

    List<AppInfo> allList(AppInfoQueryRequest request);

    SignProcess getSignProcess(Integer id, String udid);

    AppInfo getAppInfo(Integer id);

    void sendJob(AppInfo appInfo, Equipment equipment, Developer developer);

    void resign(AppInfo appInfo, Equipment equipment, Developer developer);

    void allocatTicket(AppInfo appInfo, List<Integer> tickets);

    long getAppTicketLen(AppInfo appInfo);

    Integer consumeTicket(AppInfo appInfo, Equipment equipment);

    boolean inTranscocdQueue(Integer id, Equipment equipment);

    boolean enableAppInfo(Integer id);

    boolean disableAppInfo(Integer id);

    String getUrl(Integer id);

    void initProfile(Integer appInfoId, List<Developer> developers);

}
