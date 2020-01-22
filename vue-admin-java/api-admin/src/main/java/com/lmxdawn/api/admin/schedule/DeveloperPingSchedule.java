package com.lmxdawn.api.admin.schedule;

import com.lmxdawn.api.admin.service.DeveloperPingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
public class DeveloperPingSchedule {

    @Autowired
    private DeveloperPingLogService developerPingLogService;

    /**
     * 定期检测开发者帐号的登录会话
     */
//    @Scheduled(cron = "0 4 4 * * ?")
    public void developerPing() {
        developerPingLogService.pingAll();
    }

}
