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
package com.webank.ai.fatecloud.common.Enum;

import java.io.Serializable;

public enum SiteStatusEnum    {

    NOT_JOIN(1, "Not Join"),
    JOINED(2, "Joined"),
    REMOVED(3,"Removed");

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    SiteStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

}
