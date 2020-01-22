package com.lmxdawn.api.admin.dao;

import com.lmxdawn.api.admin.entity.InstallLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InstallLogDao {

    boolean insert(InstallLog installLog);

    List<InstallLog> list(AppInfoQueryRequest appInfoQueryRequest);

    InstallLog findLastInstallLog(Integer appInfoId, String udid);


}
