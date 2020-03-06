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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webank.ai.fatecloud.global.Dict;
import com.webank.ai.fatecloud.global.StationInfo;

import java.io.Serializable;
import java.util.Arrays;

public class StationQO implements Serializable {

    private static final long serialVersionUID = 1533716358400848087L;

    private Long partyId;
    private String portalUrl;
    private String exportUrl;
    private String key;
    private String secret;
    private Byte roleType;
    private Byte nodeStatus;
    private Byte nodePublic;
    private Byte detectiveStatus;
    private Long lastDetectiveTime;
    private Long createTime;
    private Long updateTime;
    private String fDescription;
    private String organizationDesc;

    public StationQO() {
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }

    public String getExportUrl() {
        return exportUrl;
    }

    public void setExportUrl(String exportUrl) {
        this.exportUrl = exportUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Byte getRoleType() {
        return roleType;
    }

    public void setRoleType(Byte roleType) {
        this.roleType = roleType;
    }

    public Byte getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(Byte nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public Byte getNodePublic() {
        return nodePublic;
    }

    public void setNodePublic(Byte nodePublic) {
        this.nodePublic = nodePublic;
    }

    public Byte getDetectiveStatus() {
        return detectiveStatus;
    }

    public void setDetectiveStatus(Byte detectiveStatus) {
        this.detectiveStatus = detectiveStatus;
    }

    public Long getLastDetectiveTime() {
        return lastDetectiveTime;
    }

    public void setLastDetectiveTime(Long lastDetectiveTime) {
        this.lastDetectiveTime = lastDetectiveTime;
    }


    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public String getOrganizationDesc() {
        return organizationDesc;
    }

    public void setOrganizationDesc(String organizationDesc) {
        this.organizationDesc = organizationDesc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StationQO{");
        sb.append("partyId=").append(partyId);
        sb.append(", portalUrl='").append(portalUrl).append('\'');
        sb.append(", exportUrl='").append(exportUrl).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", secret='").append(secret).append('\'');
        sb.append(", roleType=").append(roleType);
        sb.append(", nodeStatus=").append(nodeStatus);
        sb.append(", nodePublic=").append(nodePublic);
        sb.append(", detectiveStatus=").append(detectiveStatus);
        sb.append(", lastDetectiveTime=").append(lastDetectiveTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", fDescription='").append(fDescription).append('\'');
        sb.append(", organizationDesc='").append(organizationDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

