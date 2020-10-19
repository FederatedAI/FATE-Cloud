package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job statistics")
public class JobStatistics implements Serializable {

    @ApiModelProperty(value = "host id")
    private String siteHostId;

    @ApiModelProperty(value = "success count")
    private String jobSuccessCount;

    @ApiModelProperty(value = "failed count")
    private String jobFailedCount;
}
