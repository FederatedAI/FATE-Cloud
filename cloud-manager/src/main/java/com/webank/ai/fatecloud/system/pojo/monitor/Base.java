package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@ApiModel(value = "Base Static")
public class Base implements Serializable {
    @ApiModelProperty(value = "total job")
    private Integer totalJobs;

    @ApiModelProperty(value = "success job")
    private Integer successJobs;

    @ApiModelProperty(value = "success job percent")
    private String successPercent;

    @ApiModelProperty(value = "running job")
    private Integer runningJobs;

    @ApiModelProperty(value = "running job percent")
    private String runningPercent;

    @ApiModelProperty(value = "waiting job")
    private Integer waitingJobs;

    @ApiModelProperty(value = "waiting job percent")
    private String waitingPercent;

    @ApiModelProperty(value = "failed job")
    private Integer failedJobs;

    @ApiModelProperty(value = "failed job percent")
    private String failedPercent;
}
