package com.lmxdawn.api.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.InstallLogDao;
import com.lmxdawn.api.admin.entity.InstallLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.service.InstallLogService;
import com.lmxdawn.api.admin.util.ThreadVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class InstallLogServiceImpl implements InstallLogService {

    @Autowired
    private InstallLogDao installLogDao;

    @Override
    public InstallLog add(InstallLog installLog) {
        installLog.setCreateTime(new Date());
        installLogDao.insert(installLog);
        return installLog;
    }

    @Override
    public List<InstallLog> list(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        request.setOwnerId(ThreadVariable.getUserId());
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return installLogDao.list(request);
    }

    @Override
    public InstallLog findLastInstallLog(Integer appInfoId, String udid) {
        return installLogDao.findLastInstallLog(appInfoId, udid);
    }
}
