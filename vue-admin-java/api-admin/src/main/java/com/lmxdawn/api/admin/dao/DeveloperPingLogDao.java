package com.lmxdawn.api.admin.dao;

import com.lmxdawn.api.admin.entity.DeveloperPingLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeveloperPingLogDao {

    boolean insert(DeveloperPingLog developerPingLog);

    List<DeveloperPingLog> list(AppInfoQueryRequest appInfoQueryRequest);
}
