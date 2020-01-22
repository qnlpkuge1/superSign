package com.lmxdawn.api.admin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Developer implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String cert;
    private Integer installLimit;
    private Integer appInfoId;
    private String appName;
    private Date createTime;


    public Developer() {
    }

    public Developer(Integer id, String username, String password, String cert) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.cert = cert;
    }
}
