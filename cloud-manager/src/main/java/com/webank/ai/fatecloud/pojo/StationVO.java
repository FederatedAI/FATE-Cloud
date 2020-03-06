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

public class StationVO implements Serializable {
    private static final long serialVersionUID = 8387373098531583752L;
    private Long partyId;

    private String portalUrl;

    private String exportUrl;

    private String key;
    private String secret;

    private RoleTypeVO roleTypeVO;

    private NodeStatusVO nodeStatusVO;

    private NodePublicVO nodePublicVO;

    private DetectiveStatusVO detectiveStatusVO;

    private Long lastDetectiveTime;

//    private String[] relatedUserList;

    private Long createTime;

    private Long updateTime;

    private String fDescription;

    private String organizationDesc;

    public StationVO() {
    }

    public StationVO(Station station) {
        this.partyId = station.getId();
        this.portalUrl = station.getPortalUrl();
        this.exportUrl = station.getExportUrl();
        JSONObject secretInfo = JSON.parseObject(station.getSecretInfo());
        if (null != secretInfo) {

            this.key = secretInfo.getString(Dict.KEY);
            this.secret = secretInfo.getString(Dict.SECRET);
        }

        switch (station.getRoleType()) {
            case -1:
                this.roleTypeVO = new RoleTypeVO(StationInfo.ROLE_TYPE_UNKNOW);
                break;
            case 1:
                this.roleTypeVO = new RoleTypeVO(StationInfo.ROLE_TYPE_APPLICATION);
                break;
            case 2:
                this.roleTypeVO = new RoleTypeVO(StationInfo.ROLE_TYPE_SUPPLIER);
                break;
            default:
        }

        switch (station.getNodeStatus()) {
            case 1:
                this.nodeStatusVO = new NodeStatusVO(StationInfo.NODE_STATUS_UNJOIN);
                break;
            case 2:
                this.nodeStatusVO = new NodeStatusVO(StationInfo.NODE_STATUS_JOINED);
                break;
            case 3:
                this.nodeStatusVO = new NodeStatusVO(StationInfo.NODE_STATUS_REMOVED);
                break;
            default:
        }

        switch (station.getNodePublic()) {
            case 1:
                this.nodePublicVO = new NodePublicVO(StationInfo.NODE_PUBLIC_YES);
                break;
            case 2:
                this.nodePublicVO = new NodePublicVO(StationInfo.NODE_PUBLIC_NO);
                break;
            default:
        }

        switch (station.getDetectiveStatus()) {
            case 1:
                this.detectiveStatusVO = new DetectiveStatusVO(StationInfo.DETECTIVE_STATUS_SURVIVE);
                break;
            case 2:
                this.detectiveStatusVO = new DetectiveStatusVO(StationInfo.DETECTIVE_STATUS_UNSURVIVE);
                break;
            default:
        }


        if (null != station.getLastDetectiveTime()) {
            this.lastDetectiveTime = station.getLastDetectiveTime().getTime();
        }

//        if (null != station.getRelatedUserList()) {
//            this.relatedUserList = station.getRelatedUserList().split(";");
//        }


        if (null != station.getCreateTime()) {
            this.createTime = null;
            this.createTime = station.getCreateTime().getTime();
        }

        if (null != station.getUpdateTime()) {
            this.updateTime = station.getUpdateTime().getTime();
        }

        this.fDescription = station.getfDescription();

        this.organizationDesc = station.getOrganizationDesc();
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

    public RoleTypeVO getRoleTypeVO() {
        return roleTypeVO;
    }

    public void setRoleTypeVO(RoleTypeVO roleTypeVO) {
        this.roleTypeVO = roleTypeVO;
    }

    public NodeStatusVO getNodeStatusVO() {
        return nodeStatusVO;
    }

    public void setNodeStatusVO(NodeStatusVO nodeStatusVO) {
        this.nodeStatusVO = nodeStatusVO;
    }

    public NodePublicVO getNodePublicVO() {
        return nodePublicVO;
    }

    public void setNodePublicVO(NodePublicVO nodePublicVO) {
        this.nodePublicVO = nodePublicVO;
    }

    public DetectiveStatusVO getDetectiveStatusVO() {
        return detectiveStatusVO;
    }

    public void setDetectiveStatusVO(DetectiveStatusVO detectiveStatusVO) {
        this.detectiveStatusVO = detectiveStatusVO;
    }

    public Long getLastDetectiveTime() {
        return lastDetectiveTime;
    }

    public void setLastDetectiveTime(Long lastDetectiveTime) {
        this.lastDetectiveTime = lastDetectiveTime;
    }

//    public String[] getRelatedUserList() {
//        return relatedUserList;
//    }
//
//    public void setRelatedUserList(String[] relatedUserList) {
//        this.relatedUserList = relatedUserList;
//    }

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
        final StringBuilder sb = new StringBuilder("StationVO{");
        sb.append("partyId=").append(partyId);
        sb.append(", portalUrl='").append(portalUrl).append('\'');
        sb.append(", exportUrl='").append(exportUrl).append('\'');
        sb.append(", key='").append(key).append('\'');
        sb.append(", secret='").append(secret).append('\'');
        sb.append(", roleTypeVO=").append(roleTypeVO);
        sb.append(", nodeStatusVO=").append(nodeStatusVO);
        sb.append(", nodePublicVO=").append(nodePublicVO);
        sb.append(", detectiveStatusVO=").append(detectiveStatusVO);
        sb.append(", lastDetectiveTime=").append(lastDetectiveTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", fDescription='").append(fDescription).append('\'');
        sb.append(", organizationDesc='").append(organizationDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
