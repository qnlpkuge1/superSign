package com.lmxdawn.api.admin.service.chain;

import com.lmxdawn.api.admin.entity.Job;

public abstract class JobProcessChain {

    public abstract void doProcess(Job job);
}
