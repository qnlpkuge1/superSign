package com.lmxdawn.api.admin.req.appInfo;

import com.lmxdawn.api.admin.req.ListPageRequest;
import lombok.Data;

@Data
public class AppInfoQueryRequest extends ListPageRequest {

    private String appName;

    private Long ownerId;

}
