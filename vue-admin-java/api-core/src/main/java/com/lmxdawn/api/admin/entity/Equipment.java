package com.lmxdawn.api.admin.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Equipment implements Serializable {

    private String product;
    private String udid;
    private String version;


}
