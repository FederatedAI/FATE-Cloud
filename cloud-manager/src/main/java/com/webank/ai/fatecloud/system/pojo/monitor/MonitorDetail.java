package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "Base Static")
public class MonitorDetail implements Serializable {

    @ApiModelProperty(value = "total insitution site detail")
    private List<InstitutionSiteBase> list;
}
