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
package com.webank.ai.fate.manager.common;
import lombok.Data;

@Data
public class CommonResponse<T> {

    private static final long serialVersionUID = -9147109334321575942L;
    private int code;
    private String message;
    private T data;


    public CommonResponse() {
    }

    public CommonResponse(int code, String message) {
        this(code, message, null);
    }

    public CommonResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CommonResponse error() {
        return error(1004, "system error");
    }

    public static CommonResponse error(String message) {
        return error(1004, message);
    }

    public static CommonResponse error(int code, String message) {
        CommonResponse r = new CommonResponse();
        r.code = code;
        r.message = message;
        return r;
    }

    public static CommonResponse ok(String message) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.message = message;
        return r;
    }

    public static CommonResponse success(Object data) {
        CommonResponse r = new CommonResponse();
        r.code = 0;
        r.message = "Success";
        r.data = data;
        return r;
    }
}
