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

import com.alibaba.fastjson.JSONObject;
import com.webank.ai.fatecloud.global.Dict;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class Station {
    private Long id;

    private String portalUrl;

    private String exportUrl;

    private String secretInfo;

    private Byte roleType;

    private Byte nodeStatus;

    private Byte nodePublic;

    private Byte detectiveStatus;

    private Date lastDetectiveTime;

    private Date createTime;

    private Date updateTime;

    private String fDescription;

    private String organizationDesc;

    public Station() {
    }

    public Station(StationQO stationQO) {
        this.id = stationQO.getPartyId();
        this.portalUrl = stationQO.getPortalUrl();
        this.exportUrl = stationQO.getExportUrl();


        this.roleType = stationQO.getRoleType();

        this.nodeStatus = stationQO.getNodeStatus();

        this.nodePublic = stationQO.getNodePublic();

        this.detectiveStatus = stationQO.getDetectiveStatus();

        if (null != stationQO.getLastDetectiveTime()) {
            this.lastDetectiveTime = new Date(stationQO.getLastDetectiveTime());
        }


        if (null != stationQO.getCreateTime()) {

            this.createTime = new Date(stationQO.getCreateTime());
        }
        if (null != stationQO.getUpdateTime()) {

            this.updateTime = new Date(stationQO.getUpdateTime());
        }
        this.fDescription = stationQO.getfDescription();

        this.organizationDesc = stationQO.getOrganizationDesc();
    }

    public Station(RegisterStationQO registerStationQO) {
        this.id = registerStationQO.getPartyId();
        this.portalUrl = registerStationQO.getPortalUrl();
        this.exportUrl = registerStationQO.getExportUrl();
        this.nodePublic = registerStationQO.getNodePublic();
        this.fDescription = registerStationQO.getfDescription();
        this.organizationDesc = registerStationQO.getOrganizationDesc();

    }

    public Station(StationVO stationVO) {
        this.id = stationVO.getPartyId();
        this.portalUrl = stationVO.getPortalUrl();
        this.exportUrl = stationVO.getExportUrl();


        if (null != stationVO.getRoleTypeVO()) {

            this.roleType = stationVO.getRoleTypeVO().getCode();
        }

        if (null != stationVO.getNodeStatusVO()) {

            this.nodeStatus = stationVO.getNodeStatusVO().getCode();
        }


        if (null != stationVO.getNodePublicVO()) {

            this.nodePublic = stationVO.getNodePublicVO().getCode();
        }

        if (null != stationVO.getDetectiveStatusVO()) {

            this.detectiveStatus = stationVO.getDetectiveStatusVO().getCode();
        }


        if (null != stationVO.getLastDetectiveTime()) {
            this.lastDetectiveTime = new Date(stationVO.getLastDetectiveTime());
        }


        if (null != stationVO.getCreateTime()) {

            this.createTime = new Date(stationVO.getCreateTime());
        }
        if (null != stationVO.getUpdateTime()) {

            this.updateTime = new Date(stationVO.getUpdateTime());
        }
        this.fDescription = stationVO.getfDescription();

        this.organizationDesc = stationVO.getOrganizationDesc();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl == null ? null : portalUrl.trim();
    }

    public String getExportUrl() {
        return exportUrl;
    }

    public void setExportUrl(String exportUrl) {
        this.exportUrl = exportUrl == null ? null : exportUrl.trim();
    }

    public String getSecretInfo() {
        return secretInfo;
    }

    public void setSecretInfo(String secretInfo) {
        this.secretInfo = secretInfo == null ? null : secretInfo.trim();
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

    public Date getLastDetectiveTime() {
        return lastDetectiveTime;
    }

    public void setLastDetectiveTime(Date lastDetectiveTime) {
        this.lastDetectiveTime = lastDetectiveTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription == null ? null : fDescription.trim();
    }

    public String getOrganizationDesc() {
        return organizationDesc;
    }

    public void setOrganizationDesc(String organizationDesc) {
        this.organizationDesc = organizationDesc == null ? null : organizationDesc.trim();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Station{");
        sb.append("id=").append(id);
        sb.append(", portalUrl='").append(portalUrl).append('\'');
        sb.append(", exportUrl='").append(exportUrl).append('\'');
        sb.append(", secretInfo='").append(secretInfo).append('\'');
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