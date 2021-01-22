package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("TwoSiteBase")
public class TwoSiteBase implements Serializable {

    @ApiModelProperty(value = "institution")
    private String institution;

    @ApiModelProperty(value = "institutionSiteName")
    private String institutionSiteName;

    @ApiModelProperty(value = "siteName")
    private String siteName;

    @ApiModelProperty(value = "base Static")
    private Base base;
}