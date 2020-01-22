package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.entity.DeveloperPingLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;

import java.util.List;

public interface DeveloperPingLogService {

    void pingAll();

    List<DeveloperPingLog> list(AppInfoQueryRequest request);

}
