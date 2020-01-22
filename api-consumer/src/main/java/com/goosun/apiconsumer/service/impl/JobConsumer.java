package com.goosun.apiconsumer.service.impl;

import com.goosun.apiconsumer.service.chain.JobProcessChain;
import com.lmxdawn.api.admin.entity.Job;
import com.lmxdawn.api.admin.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;

@Service
public class JobConsumer {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${app.client-mode}")
    private String clientMode;

    private boolean alive = true;

    @Async
    public void run() {
        if (!clientMode.equals("consumer")) {
            return;
        }
        Map<String, JobProcessChain> chainMap = applicationContext.getBeansOfType(JobProcessChain.class);


        while (alive) {
            Object obj = redisService.lpop(Constant.TRANSCODE_QUEUE);

            try {
                if (obj == null) {
                    Thread.sleep(1000);
                    System.out.print(".");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (obj instanceof Job) {
                chainMap.forEach((key, chain) -> {
                    chain.doProcess((Job) obj);
                });
            }
        }
    }

    @PreDestroy
    public void destory(){
        this.alive = false;
    }

}
