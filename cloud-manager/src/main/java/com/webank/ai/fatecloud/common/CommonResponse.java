/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
