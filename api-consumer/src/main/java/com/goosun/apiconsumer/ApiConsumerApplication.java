package com.goosun.apiconsumer;

import com.goosun.apiconsumer.service.impl.JobConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApiConsumerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ApiConsumerApplication.class, args);
        JobConsumer jobConsumer = context.getBean(JobConsumer.class);
        if (jobConsumer != null) {
            jobConsumer.run();
        }
    }

}
