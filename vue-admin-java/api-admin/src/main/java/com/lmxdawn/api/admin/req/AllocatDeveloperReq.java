package com.lmxdawn.api.admin.req;

import lombok.Data;

import java.util.List;

@Data
public class AllocatDeveloperReq {

    private Integer appInfoId;
    private List<Integer> developerIds;
}
