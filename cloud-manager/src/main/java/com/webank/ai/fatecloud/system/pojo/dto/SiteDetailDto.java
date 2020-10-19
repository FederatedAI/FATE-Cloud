package com.webank.ai.fatecloud.system.pojo.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.webank.ai.fatecloud.common.SecretInfo;
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
    }

    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "site partyid")
    private Long partyId;

    @ApiModelProperty(value = "site appkey && secret")
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
}
