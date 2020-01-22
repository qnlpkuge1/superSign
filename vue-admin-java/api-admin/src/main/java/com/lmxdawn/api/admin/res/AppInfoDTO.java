package com.lmxdawn.api.admin.res;

import com.lmxdawn.api.admin.entity.app.AppInfo;
import lombok.Data;

@Data
public class AppInfoDTO extends AppInfo {

    private long balance;
}
