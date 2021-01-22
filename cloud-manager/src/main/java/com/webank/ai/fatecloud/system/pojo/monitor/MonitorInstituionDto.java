package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorInstituion")
public class MonitorInstituionDto implements Serializable {
    @ApiModelProperty(value = "total insitution ")
    private List<InstitutionBase> data;
}
