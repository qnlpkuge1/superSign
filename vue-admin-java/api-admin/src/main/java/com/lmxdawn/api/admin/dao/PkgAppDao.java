package com.lmxdawn.api.admin.dao;

import com.lmxdawn.api.admin.entity.PkgApp;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PkgAppDao {

    boolean insert(PkgApp pkgApp);

    List<PkgApp> ownerList(AppInfoQueryRequest appInfoQueryRequest);

    PkgApp get(Integer id);

    boolean updateMbconfig(PkgApp pkgApp);

}
