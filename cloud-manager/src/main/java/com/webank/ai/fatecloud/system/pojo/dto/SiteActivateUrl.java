package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "Acitvate info for site")
public class SiteActivateUrl extends FederatedSiteManagerDo implements Serializable {

    public SiteActivateUrl(FederatedSiteManagerDo federatedSiteManagerDo, Integer role,String federationName,Long federatedId,String groupName) {
        this.setId(federatedId);
        this.setSiteName(federatedSiteManagerDo.getSiteName());
        this.setPartyId(federatedSiteManagerDo.getPartyId());
        this.setSecretInfo(federatedSiteManagerDo.getSecretInfo());
        this.setNetworkAccessEntrances(federatedSiteManagerDo.getNetworkAccessEntrances());
        this.setNetworkAccessExits(federatedSiteManagerDo.getNetworkAccessExits());
        this.setInstitutions(federatedSiteManagerDo.getInstitutions());
        this.setFederatedOrganization(federationName);
        this.setRole(role);
        this.setGroupName(groupName);
    }

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role = 0;

    @ApiModelProperty(value = "Federated Organization")
    private String federatedOrganization;

    @ApiModelProperty(value = "group name")
    private String groupName;
}
