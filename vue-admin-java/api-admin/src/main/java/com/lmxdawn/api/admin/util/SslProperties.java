package com.lmxdawn.api.admin.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ssl")
@Data
public class SslProperties {

    private String mbcrt;
    private String mbkey;
    private String cacrt;

}
