package com.webank.ai.fatecloud.system.pojo.monitor;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = " InstitutionSiteBase")
public class InstitutionSiteBase extends Base {

    @ApiModelProperty(value = "institution")
    private String institution;

    @ApiModelProperty(value = "siteName")
    private String siteName;
}