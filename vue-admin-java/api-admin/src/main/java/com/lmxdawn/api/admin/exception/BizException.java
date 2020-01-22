package com.lmxdawn.api.admin.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private Integer code;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
