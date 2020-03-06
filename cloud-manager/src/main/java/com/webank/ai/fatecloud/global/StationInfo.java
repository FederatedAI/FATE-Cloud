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

public enum StationInfo {
    ROLE_TYPE_UNKNOW((byte) -1, "未知", "Unknown"),
    ROLE_TYPE_APPLICATION((byte) 1, "应用方", "guest"),
    ROLE_TYPE_SUPPLIER((byte) 2, "提供方", "host"),
    NODE_STATUS_UNJOIN((byte) 1, "待加入", "Not Joined"),
    NODE_STATUS_JOINED((byte) 2, "已加入", "Joined"),
    NODE_STATUS_REMOVED((byte) 3, "已剔除", "Removed"),
    NODE_PUBLIC_YES((byte) 1, "是", "Yes"),
    NODE_PUBLIC_NO((byte) 2, "否", "No"),
    DETECTIVE_STATUS_SURVIVE((byte) 1, "存活", "Survive"),
    DETECTIVE_STATUS_UNSURVIVE((byte) 2, "非存活", "Not Survive");


    private byte statusCode;
    private String description;
    private String englishDescription;

    public byte getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    StationInfo(byte statusCode, String description, String englishDescription) {
        this.statusCode = statusCode;
        this.description = description;
        this.englishDescription = englishDescription;
    }
}
