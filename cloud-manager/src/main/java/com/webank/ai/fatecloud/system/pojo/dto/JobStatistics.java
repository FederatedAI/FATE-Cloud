package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job statistics")
@Data
public class JobStatistics implements Serializable {

    @ApiModelProperty(value = "host name")
    private String siteHostName;

    @ApiModelProperty(value = "success count")
    private String jobSuccessCount;

    @ApiModelProperty(value = "failed count")
    private String jobFailedCount;
}
