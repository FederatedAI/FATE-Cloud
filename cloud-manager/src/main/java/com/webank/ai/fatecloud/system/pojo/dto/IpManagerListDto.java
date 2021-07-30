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
package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("ip manager list rep")
public class IpManagerListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public IpManagerListDto(FederatedSiteManagerDo federatedSiteManagerDo) {
        this.partyId = federatedSiteManagerDo.getPartyId();
        this.siteName = federatedSiteManagerDo.getSiteName();
        this.institutions = federatedSiteManagerDo.getInstitutions();
        this.networkAccessEntrancesOld=federatedSiteManagerDo.getNetworkAccessEntrances();
        this.networkAccessExitsOld=federatedSiteManagerDo.getNetworkAccessExits();
        this.updateTime=federatedSiteManagerDo.getCreateTime().getTime();
    }
    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "site belongs to institutions")
    private String institutions;

    @ApiModelProperty(value = "site partyid")
    private Long partyId;

    @ApiModelProperty(value = "role,1:guest,2:host")
    private Integer role;

    @ApiModelProperty(value = "network access entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "network access entrances old")
    private String networkAccessEntrancesOld;

    @ApiModelProperty(value = "network access exits old")
    private String networkAccessExitsOld;

    @ApiModelProperty(value = "exchange name")
    private String exchangeName;

    @ApiModelProperty(value = "secure status")
    private Integer secureStatus;

    @ApiModelProperty(value = "polling status")
    private Integer pollingStatus;

    @ApiModelProperty(value = "update time")
    private Long updateTime;

    @ApiModelProperty(value = "log deal status,0:no deal,1:agreed,2:rejected")
    private Integer status;

    @ApiModelProperty(value = "case_id")
    private String caseId;

    @ApiModelProperty(value = "history")
    private Integer history;
}
