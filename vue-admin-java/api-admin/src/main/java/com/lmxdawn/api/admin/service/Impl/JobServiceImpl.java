package com.lmxdawn.api.admin.service.Impl;

import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.service.JobService;
import com.lmxdawn.api.admin.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private RedisService redisService;

    @Override
    public void sendJob(Job job) {
        redisService.rpush(Constant.TRANSCODE_QUEUE, job);
    }
}
