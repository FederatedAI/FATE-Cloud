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
public class JobStatisticsSummaryTodaySiteEachDto implements Serializable {

    @ApiModelProperty(value = "site name")
    private String site;

    @ApiModelProperty(value = "party id")
    private String partyId;

    @ApiModelProperty(value = "total failed job count")
    private Integer failedJobCount;

    @ApiModelProperty(value = "total successfuljob count")
    private Integer successJobCount;
}
