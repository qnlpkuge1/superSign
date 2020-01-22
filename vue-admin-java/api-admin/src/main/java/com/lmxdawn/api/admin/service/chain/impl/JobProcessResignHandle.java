package com.lmxdawn.api.admin.service.chain.impl;

import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.admin.service.chain.JobProcessChain;
import com.lmxdawn.api.admin.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobProcessResignHandle extends JobProcessChain {

    @Autowired
    private AppInfoService appInfoService;

    @Override
    public void doProcess(Job job) {
        if (Constant.RESIGN_STRATEGY.equals(job.getType())) {
            appInfoService.resign(job.getAppInfo(), job.getEquipment(), job.getDeveloper());
        }
    }
}
