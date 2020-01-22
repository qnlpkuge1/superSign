package com.lmxdawn.api.admin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeveloperPingLog implements Serializable {

    private Integer id;
    private Integer developerId;
    private String developerName;
    private boolean status;
    private String result = "";
    private Date createTime;
}
