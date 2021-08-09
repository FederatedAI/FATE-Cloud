package com.webank.ai.fatecloud.common.exception;

import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;

public class LogicException extends RuntimeException {
    private final ReturnCodeEnum returnCodeEnum;
    private final Object data;

    public LogicException(ReturnCodeEnum returnCodeEnum) {
        super(returnCodeEnum.getMessage());
        this.returnCodeEnum = returnCodeEnum;
        this.data = null;
    }

    public LogicException(ReturnCodeEnum returnCodeEnum, Object data) {
        super(returnCodeEnum.getMessage());
        this.returnCodeEnum = returnCodeEnum;
        this.data = data;
    }

    public ReturnCodeEnum getReturnCode() {
        return returnCodeEnum;
    }

    public Object getData() {
        return data;
    }

    public static LogicException getInstance(ReturnCodeEnum returnCodeEnum) {
        return new LogicException(returnCodeEnum);
    }

    public static LogicException getInstance(ReturnCodeEnum returnCodeEnum, Object data) {
        return new LogicException(returnCodeEnum, data);
    }

    public static void throwInstance(ReturnCodeEnum returnCodeEnum) throws LogicException {
        throw getInstance(returnCodeEnum);
    }

    public static void throwInstance(ReturnCodeEnum returnCodeEnum, Object data) throws LogicException {
        throw getInstance(returnCodeEnum, data);
    }
}
