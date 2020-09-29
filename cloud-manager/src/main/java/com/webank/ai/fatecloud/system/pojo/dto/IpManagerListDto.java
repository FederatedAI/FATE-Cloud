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

    @ApiModelProperty(value = "update time")
    private Long updateTime;

    @ApiModelProperty(value = "log deal status,0:no deal,1:agreed,2:rejected")
    private Integer status;

    @ApiModelProperty(value = "case_id")
    private String caseId;

    @ApiModelProperty(value = "history")
    private Integer history;
}
