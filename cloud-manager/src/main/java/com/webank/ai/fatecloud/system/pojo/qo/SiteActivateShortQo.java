package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ApiModel(value = "site activate short link")
public class SiteActivateShortQo {

    @ApiModelProperty(value = "federated registration link")
    private String registrationLink;

    @ApiModelProperty(value = "network access entrance")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "party rollSite network exit")
    private String networkAccessExits;

    @ApiModelProperty(value = "secure_status")
    private Integer secureStatus;

    @ApiModelProperty(value = "polling_status")
    private Integer pollingStatus;
}
