package com.lmxdawn.api.admin.dao;

import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppInfoDao {

    boolean insert(AppInfo appInfo);

    boolean updateInstallLimit(Integer id, Integer installLimit);

    AppInfo get(Integer id);

    List<AppInfo> list(AppInfoQueryRequest appInfoQueryRequest);

    boolean updateIpa(AppInfo appInfo);

    boolean enableAppInfo(Integer id);

    boolean disableAppInfo(Integer id);

}
