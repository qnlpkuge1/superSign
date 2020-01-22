package com.lmxdawn.api.admin.res;

import com.lmxdawn.api.admin.entity.Developer;
import lombok.Data;

@Data
public class DeveloperDTO extends Developer {

    private long balance;
}
