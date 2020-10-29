package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job summary for today, site dimension")
public class JobStatisticsSummaryTodaySiteAllDto implements Serializable {
    @ApiModelProperty(value = "total site count")
    private Integer siteCount;

    @ApiModelProperty(value = "total failed job count")
    private Integer failedJobCount;

    @ApiModelProperty(value = "total successful job count")
    private Integer successJobCount;

}
