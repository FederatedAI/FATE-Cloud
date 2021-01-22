package com.webank.ai.fatecloud.system.pojo.monitor;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "SiteBase")
public class SiteBase implements Serializable {

    @ApiModelProperty(value = "siteName")
    private String siteName;

    @ApiModelProperty(value = "base")
    private Base base;
}