package com.lmxdawn.api.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignProcess implements Serializable {

    private int process;

    private String status;

    private String plist;

    public SignProcess() {
    }


    public SignProcess(int process) {
        this.process = process;
        this.status = "processing";
        this.plist = "";
    }

    public SignProcess(int process, String status, String plist) {
        this.process = process;
        this.status = status;
        this.plist = plist;
    }
}
