package com.lmxdawn.api.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DeveloperDao;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.InstallLog;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.exception.BizException;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.admin.service.DeveloperService;
import com.lmxdawn.api.admin.service.InstallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    @Autowired
    private DeveloperDao developerDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private InstallLogService installLogService;

    @Override
    public Developer add(Developer developer) {
        developer.setCreateTime(new Date());
        developer.setAppInfoId(0);
        developer.setAppName("");
        developerDao.insert(developer);
        initTicket(developer);
        return developer;
    }

    private void initTicket(Developer developer) {
        String key = getKey(developer);
        List<Integer> tickets = new ArrayList<>();
        for (int i = 0; i < developer.getInstallLimit(); i++) {
            tickets.add(developer.getId());
        }
        redisService.set(key, tickets);
    }

    private String getKey(Developer developer) {
        return String.format("developTicket::%s", developer.getId());
    }

    @Override
    public boolean allocats(Integer appInfoId, List<Integer> developerIds) {
        if (CollectionUtils.isEmpty(developerIds)) {
            return false;
        }
        List<Developer> developers = developerDao.findByIds(developerIds);
        if (CollectionUtils.isEmpty(developers)) {
            return false;
        }

        for (Developer developer : developers) {
            allocat(appInfoId, developer);
        }
        appInfoService.initProfile(appInfoId, developers);

        return true;
    }

    @Override
    public Developer consume(AppInfo appInfo, Equipment equipment) {

        InstallLog installLog = installLogService.findLastInstallLog(appInfo.getId(), equipment.getUdid());
        if (installLog != null) {
            installLog.setDeduction(0);
            installLogService.add(installLog);
            return developerDao.get(installLog.getDeveloperId());
        }

        Integer developerId = appInfoService.consumeTicket(appInfo, equipment);
        if (developerId != null) {
            Developer developer = developerDao.get(developerId);
            installLog = new InstallLog(appInfo, equipment, developer);
            installLog.setDeduction(1);
            installLogService.add(installLog);
            return developer;
        }
        return null;
    }

    private void allocat(Integer appInfoId, Developer developer) {
        AppInfo appInfo = appInfoService.getAppInfo(appInfoId);
        if (appInfo == null) {
            throw new BizException(12100, "未找到分配到的应用");
        }
        developer.setAppInfoId(appInfoId);
        developer.setAppName(appInfo.getAppName());

        String key = getKey(developer);
        Object ticketObj = redisService.get(key);
        if (ticketObj != null) {
            List<Integer> tickets = (List<Integer>) ticketObj;
            appInfoService.allocatTicket(appInfo, tickets);
            developerDao.updateAllocat(developer);
            redisService.del(key);
        } else {
            throw new BizException(12200, "该开发者帐号不存可以分配的份额");
        }
    }

    @Override
    public List<Developer> list(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return developerDao.list(request);
    }

    @Override
    public List<Developer> findUnallocat() {
        return developerDao.findUnallocat();
    }

    @Override
    public long getTicketLen(Developer developer) {
        Object obj = redisService.get(getKey(developer));
        if(obj!=null){
            return ((List<Integer>)obj).size();
        }
        return 0L;
    }

    @Override
    public void resetInstall(Integer developerId, Integer install) {
        developerDao.resetInstall(developerId, install);
        Developer developer = developerDao.get(developerId);
        initTicket(developer);
    }

    @Override
    public List<Developer> findAll() {
        return developerDao.findAll();
    }
}
