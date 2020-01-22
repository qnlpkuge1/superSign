package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.PkgApp;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;

import java.util.List;

/**
 * 封包逻辑
 */
public interface PkgAppService {

    PkgApp add(PkgApp pkgApp);

    PkgApp get(Integer id);

    List<PkgApp> ownerList(AppInfoQueryRequest request);

    String getUrl(Integer id);

}
