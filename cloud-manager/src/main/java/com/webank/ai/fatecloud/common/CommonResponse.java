package com.webank.ai.fatecloud.common;


import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommonResponse<T> {


    @ApiModelProperty(value = "code")
    private int code;

    @ApiModelProperty(value = "msg")
    private String msg;

    @ApiModelProperty(value = "resp data")
    private T data;


    public CommonResponse() {
    }

    public CommonResponse(int code, String msg) {
        this(code, msg, null);
    }

    public CommonResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResponse(ReturnCodeEnum returnCodeEnum) {
        this.code = returnCodeEnum.getCode();
        this.msg = returnCodeEnum.getMessage();
    }

    public CommonResponse(ReturnCodeEnum returnCodeEnum, T data) {
        this.code = returnCodeEnum.getCode();
        this.msg = returnCodeEnum.getMessage();
        this.data = data;
    }

    public static CommonResponse error() {
        return error(500, "system error");
    }

    public static CommonResponse error(String msg) {
        return error(500, msg);
    }

    public static CommonResponse error(Object data, int code, String msg) {
        CommonResponse r = new CommonResponse();
        r.code = code;
        r.msg = msg;
        r.data = data;
        return r;
    }

    public static CommonResponse error(int code, String msg) {
        CommonResponse r = new CommonResponse();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static CommonResponse ok(String msg) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.msg = msg;
        return r;
    }

    public static CommonResponse success(Object data) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static CommonResponse success(Object data, Integer count) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static CommonResponse success(Object data, String msg) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.msg = msg;
        r.data = data;
        return r;
    }
}
