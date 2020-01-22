package com.lmxdawn.api.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.PkgAppDao;
import com.lmxdawn.api.admin.entity.PkgApp;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.service.FileStroeService;
import com.lmxdawn.api.admin.service.PkgAppService;
import com.lmxdawn.api.admin.service.UDIDService;
import com.lmxdawn.api.admin.util.ImageUtil;
import com.lmxdawn.api.admin.util.ThreadVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PkgAppServiceImpl implements PkgAppService {

    @Value("${app.appServer}")
    private String appServer;

    @Autowired
    private PkgAppDao pkgAppDao;

    @Autowired
    private FileStroeService fileStroeService;

    @Autowired
    private UDIDService udidService;

    @Override
    public PkgApp add(PkgApp pkgApp) {

        pkgApp.setMbConfig("");
        pkgApp.setOwnerId(ThreadVariable.getUserId());
        pkgApp.setStatus(1);
        pkgApp.setCreateTime(new Date());
        pkgAppDao.insert(pkgApp);
        pkgApp.setMbConfig(genPkgApp(pkgApp));
        pkgAppDao.updateMbconfig(pkgApp);
        return pkgApp;
    }

    private String genPkgApp(PkgApp pkgApp) {
        String objectName = String.format("pkgApp/%s/udid.mobileconfig", pkgApp.getId());
        String mbconfig = buildMbconfig(pkgApp);
        return fileStroeService.uploadString(objectName, mbconfig);
    }

    @Override
    public PkgApp get(Integer id) {
        return pkgAppDao.get(id);
    }

    @Override
    public List<PkgApp> ownerList(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        request.setOwnerId(ThreadVariable.getUserId());
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return pkgAppDao.ownerList(request);
    }

    @Override
    public String getUrl(Integer id) {
        return String.format("%s/pkgApp/%s", appServer, id);
    }

    private String buildMbconfig(PkgApp pkgApp) {
        String iconBase64 = ImageUtil.getImageByUrl(pkgApp.getIconPath());
        String domain = getDomain(pkgApp.getUrl());
        if (pkgApp.getType().equals("removePassword")) {
            return String.format(udidService.getMbconfigTemp(pkgApp.getType()), iconBase64, pkgApp.getName(), pkgApp.getName(), domain, getUuid(), pkgApp.getUrl(),
                    domain, getUuid(), pkgApp.getRemovePassword(), pkgApp.getName(), pkgApp.getName(), domain, getUuid());
        } else if (pkgApp.getType().equals("removePassword")) {
            return String.format(udidService.getMbconfigTemp(pkgApp.getType()), iconBase64, pkgApp.getName(), pkgApp.getName(), domain, getUuid(), pkgApp.getUrl(),
                    pkgApp.getName(), pkgApp.getName(), domain, getUuid());
        } else {
            return String.format(udidService.getMbconfigTemp(pkgApp.getType()), iconBase64, pkgApp.getName(), pkgApp.getName(), domain, getUuid(), pkgApp.getUrl(),
                    pkgApp.getName(), pkgApp.getName(), domain, getUuid());
        }
    }

    private String getUuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    private String getDomain(String url) {
        int index = url.indexOf("://");
        if (index > 0) {
            return url.substring(index + 3);
        }
        return url;
    }
}
