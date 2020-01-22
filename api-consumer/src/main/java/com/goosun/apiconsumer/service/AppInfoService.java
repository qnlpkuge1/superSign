package com.goosun.apiconsumer.service;

import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;

public interface AppInfoService {

    void resign(AppInfo appInfo, Equipment equipment, Developer developer);

}
