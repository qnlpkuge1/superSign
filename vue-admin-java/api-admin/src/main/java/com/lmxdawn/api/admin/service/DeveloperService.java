package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;

import java.util.List;

public interface DeveloperService {

    Developer add(Developer developer);

    List<Developer> list(AppInfoQueryRequest request);

    boolean allocats(Integer appInfoId, List<Integer> developerIds);

    Developer consume(AppInfo appInfo, Equipment equipment);

    List<Developer> findUnallocat();

    long getTicketLen(Developer developer);

    void resetInstall(Integer developerId,Integer install);

    List<Developer> findAll();

}
