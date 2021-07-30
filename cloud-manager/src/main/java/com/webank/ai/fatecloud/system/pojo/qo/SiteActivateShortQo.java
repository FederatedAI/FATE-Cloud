package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ApiModel(value = "site activate short link")
public class SiteActivateShortQo {

    @ApiModelProperty(value = "federated registration link")
    private String registrationLink;

    @ApiModelProperty(value = "roll site address")
    private String rollSiteAddress;

    @ApiModelProperty(value = "network access entrance")
    private String networkAccessEntrance;

    @ApiModelProperty(value = "network access exit")
    private String networkAccessExit;
}
