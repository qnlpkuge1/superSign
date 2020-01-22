package com.lmxdawn.api.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PkgApp {

    private Integer id;
    private String iconPath;
    private String name;
    private String url;
    // normal:普通；removePassword:需要密码删除；unRomve: 不可删除
    private String type;
    private String removePassword;
    private Integer status;
    private String mbConfig;
    private Long ownerId;
    private Date createTime;

}
