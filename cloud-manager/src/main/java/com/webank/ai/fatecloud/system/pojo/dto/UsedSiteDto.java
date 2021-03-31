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
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "used site details")
public class UsedSiteDto implements Serializable {

    public UsedSiteDto(FederatedSiteManagerDo federatedSiteManagerDo) {
        this.id = federatedSiteManagerDo.getId();
        this.partyId = federatedSiteManagerDo.getPartyId();
        this.siteName = federatedSiteManagerDo.getSiteName();
        this.institutions = federatedSiteManagerDo.getInstitutions();
        this.createTime = federatedSiteManagerDo.getCreateTime().getTime();
        this.status = federatedSiteManagerDo.getStatus();
    }

    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "site partyid")
    private Long partyId;

    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "site belongs to institutions")
    private String institutions;

    @ApiModelProperty(value = "create time")
    private Long createTime;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
    private Integer status;
}
