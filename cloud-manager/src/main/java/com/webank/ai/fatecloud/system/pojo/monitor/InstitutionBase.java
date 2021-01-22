package com.webank.ai.fatecloud.system.pojo.monitor;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "MonitorInstitutionTotal")
public class InstitutionBase implements Serializable {

    @ApiModelProperty(value = "institution")
    private String institution;

    @ApiModelProperty(value = "base Static")
    private Base base;
}