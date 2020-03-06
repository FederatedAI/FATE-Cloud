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

import java.io.Serializable;

public class RegisterStationQO implements Serializable {
    private Long partyId;
    private String portalUrl;
    private String exportUrl;
    private Byte nodePublic;
    private String fDescription;
    private String organizationDesc;

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

    public Byte getNodePublic() {
        return nodePublic;
    }

    public void setNodePublic(Byte nodePublic) {
        this.nodePublic = nodePublic;
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
        final StringBuilder sb = new StringBuilder("RegisterStationQO{");
        sb.append("partyId=").append(partyId);
        sb.append(", portalUrl='").append(portalUrl).append('\'');
        sb.append(", exportUrl='").append(exportUrl).append('\'');
        sb.append(", nodePublic=").append(nodePublic);
        sb.append(", fDescription='").append(fDescription).append('\'');
        sb.append(", organizationDesc='").append(organizationDesc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
