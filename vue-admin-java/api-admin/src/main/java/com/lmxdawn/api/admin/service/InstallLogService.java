package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.entity.InstallLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;

import java.util.List;

public interface InstallLogService {

    InstallLog add(InstallLog installLog);

    List<InstallLog> list(AppInfoQueryRequest request);

    InstallLog findLastInstallLog(Integer appInfoId, String udid);

}
