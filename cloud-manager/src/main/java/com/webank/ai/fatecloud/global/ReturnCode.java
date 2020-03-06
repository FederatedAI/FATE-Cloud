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
package com.webank.ai.fatecloud.global;

public enum ReturnCode {
    SUCCESS(0, "Success"),
    PARAMETERS_ERROR(100, "Wrong Parameters!"),
    AUTHORIZATION_ERROR(101, "Authorization Failure!"),
    DATA_ERROR(102, "Failure to get station data!"),
    STATION_REGISTER_ERROR(103, "Station does not register!"),
    STATION_ERROR(104, "Station out of access!"),
    CREATE_ERROR(105, "Fail to create new station!"),
    SYSTEM_ERROR(106, "System error!"),
    REGISTER_ERROR(107, "Station has been removed!"),
    STATUS_ERROR(108, "Station has been registered, it can't be changed!"),
    CHECK_ERROR(109, "Check failure!"),

    ;
    private final int code;
    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
