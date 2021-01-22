package com.webank.ai.fatecloud.system.pojo.monitor;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = " InstitutionSiteBase")
public class InstitutionSiteBase implements Serializable {

    @ApiModelProperty(value = "institution")
private String institution;

    @ApiModelProperty(value = "siteName")
    private String siteName;

    @ApiModelProperty(value = "base Static")
    private Base base;
}