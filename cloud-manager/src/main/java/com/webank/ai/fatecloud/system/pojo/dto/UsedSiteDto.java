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
