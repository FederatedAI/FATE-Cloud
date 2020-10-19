package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("fate version update")
public class VersionUpdateQo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "fate version")
    private String fateVersion;

    @ApiModelProperty(value = "fate serving version")
    private String fateServingVersion;

    @ApiModelProperty(value = "fate component version")
    private String componentVersion;
}
