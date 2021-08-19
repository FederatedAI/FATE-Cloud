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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotation.TableField;
import com.webank.ai.fatecloud.common.SecretInfo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.RollSiteDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("site detail")
public class SiteDetailDto implements Serializable {
    public SiteDetailDto(FederatedSiteManagerDo federatedSiteManagerDo) {
        this.id = federatedSiteManagerDo.getId();
        this.siteName = federatedSiteManagerDo.getSiteName();
        this.partyId = federatedSiteManagerDo.getPartyId();
        if (federatedSiteManagerDo.getSecretInfo() != null) {
            String secretInfoString = federatedSiteManagerDo.getSecretInfo();
            this.secretInfo = JSON.parseObject(secretInfoString, new TypeReference<SecretInfo>() {
            });
        }
        this.registrationLink = federatedSiteManagerDo.getRegistrationLink();
        this.networkAccessEntrances = federatedSiteManagerDo.getNetworkAccessEntrances();
        this.networkAccessExits = federatedSiteManagerDo.getNetworkAccessExits();
        this.institutions = federatedSiteManagerDo.getInstitutions();
        this.fateVersion = federatedSiteManagerDo.getFateVersion();
        this.fateServingVersion = federatedSiteManagerDo.getFateServingVersion();
        this.componentVersion = federatedSiteManagerDo.getComponentVersion();
        this.status = federatedSiteManagerDo.getStatus();
        this.detectiveStatus = federatedSiteManagerDo.getDetectiveStatus();
        this.network = federatedSiteManagerDo.getNetwork();
        this.protocol = federatedSiteManagerDo.getProtocol();
        this.encryptType = federatedSiteManagerDo.getEncryptType();
        if (federatedSiteManagerDo.getCreateTime() != null) {
            this.createTime = federatedSiteManagerDo.getCreateTime().getTime();
        }
        if (federatedSiteManagerDo.getActivationTime() != null) {
            this.activationTime = federatedSiteManagerDo.getActivationTime().getTime();
        }
        if (federatedSiteManagerDo.getUpdateTime() != null) {
            this.updateTime = federatedSiteManagerDo.getUpdateTime().getTime();
        }
        this.groupId = federatedSiteManagerDo.getGroupId();
        if (federatedSiteManagerDo.getFederatedGroupSetDo() != null) {
            this.role = federatedSiteManagerDo.getFederatedGroupSetDo().getRole();
        }
        if (federatedSiteManagerDo.getFederatedGroupSetDo() != null) {
            this.groupName = federatedSiteManagerDo.getFederatedGroupSetDo().getGroupName();
        }
        this.exchangeId = federatedSiteManagerDo.getExchangeId();
    }

    public SiteDetailDto(FederatedSiteManagerDo federatedSiteManagerDo, PartyDetailsDto partyDetailsDto) {
        this(federatedSiteManagerDo);
        if (partyDetailsDto != null) {
            this.secureStatus = partyDetailsDto.getSecureStatus();
            this.pollingStatus = partyDetailsDto.getPollingStatus();
            this.exchangeId = partyDetailsDto.getExchangeId();
            this.exchangeName = partyDetailsDto.getExchangeName();
            this.vipEntrance = partyDetailsDto.getVipEntrance();
            this.rollSiteNetworkAccessExits = partyDetailsDto.getRollSiteDoList()
                    .stream()
                    .map(RollSiteDo::getNetworkAccessExit)
                    .collect(Collectors.joining(";", "", ";"));
        }
    }

    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "site party id")
    private Long partyId;

    @ApiModelProperty(value = "site app key && secret")
    private SecretInfo secretInfo;

    @ApiModelProperty(value = "federated registration link")
    private String registrationLink;

    @ApiModelProperty(value = "network access entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "site belongs to institutions")
    private String institutions;

    @ApiModelProperty(value = "fate version")
    private String fateVersion;

    @ApiModelProperty(value = "fate serving version")
    private String fateServingVersion;

    @ApiModelProperty(value = "component version")
    private String componentVersion;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
    private Integer status;

    @ApiModelProperty(value = "protocol")
    private String protocol;

    @ApiModelProperty(value = "network")
    private String network;

    @ApiModelProperty(value = "create time")
    private Long createTime;

    @ApiModelProperty(value = "activation Time")
    private Long activationTime;

    @ApiModelProperty(value = "update time")
    private Long updateTime;

    @ApiModelProperty(value = "party id  group")
    private Long groupId;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role;

    @ApiModelProperty(value = "group name")
    private String groupName;

    @ApiModelProperty(value = "federated organization name")
    private String federatedOrganization;

    @ApiModelProperty(value = "federated organization id")
    private Long federatedOrganizationId;

    @ApiModelProperty(value = "site detective status")
    private Integer detectiveStatus;

    @ApiModelProperty(value = "encrypt type")
    private Integer encryptType;

    @ApiModelProperty(value = "secure status, 1 = false, 2 = true")
    private Integer secureStatus;

    @ApiModelProperty(value = "polling status, 1 = false, 2 = true")
    private Integer pollingStatus;

    @ApiModelProperty(value = "exchange id")
    private Long exchangeId;

    @ApiModelProperty(value = "exchange name")
    private String exchangeName;

    @ApiModelProperty(value = "exchange vip entrances")
    private String vipEntrance;

    @ApiModelProperty(value = "current roll site exit list in under exchange")
    private String rollSiteNetworkAccessExits;
}
