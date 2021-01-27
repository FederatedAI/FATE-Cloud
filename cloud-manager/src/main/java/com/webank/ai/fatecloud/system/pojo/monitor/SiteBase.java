package com.webank.ai.fatecloud.system.pojo.monitor;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "SiteBase")
public class SiteBase extends Base {

    @ApiModelProperty(value = "siteName")
    private String siteName;
}