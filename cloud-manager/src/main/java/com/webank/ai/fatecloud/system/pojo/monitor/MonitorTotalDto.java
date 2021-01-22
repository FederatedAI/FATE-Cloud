package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorTotalDto")
public class MonitorTotalDto implements Serializable {
    @ApiModelProperty(value = "total monitor")
    private Base base;

    @ApiModelProperty(value = "total insitution monitor")
    private List<InstitutionBase> data;
}
