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
package com.webank.ai.fatecloud.pojo;

import com.webank.ai.fatecloud.global.StationInfo;

import java.io.Serializable;

public class RoleTypeVO implements Serializable {
    private static final long serialVersionUID = 8674917420725192477L;
    private byte code;
    private String description;
    private String englishDescription;

    public RoleTypeVO() {
    }

    public RoleTypeVO(byte code, String description, String englishDescription) {
        this.code = code;
        this.description = description;
        this.englishDescription = englishDescription;
    }
    public RoleTypeVO(StationInfo stationInfo) {
        this.code = stationInfo.getStatusCode();
        this.description = stationInfo.getDescription();
        this.englishDescription = stationInfo.getEnglishDescription();
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public void setEnglishDescription(String englishDescription) {
        this.englishDescription = englishDescription;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DetectiveStatus{");
        sb.append("code=").append(code);
        sb.append(", description='").append(description).append('\'');
        sb.append(", englishDescription='").append(englishDescription).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
