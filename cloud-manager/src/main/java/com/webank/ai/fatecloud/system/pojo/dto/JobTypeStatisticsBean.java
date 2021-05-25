package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job type statistics bean")
public class JobTypeStatisticsBean extends JobCostTimeDto implements Serializable  {
    private static final long serialVersionUID = 1L;

    private String type;

    private long successfulJobs;
    private String successfulRatio;
    private long failedJobs;
    private String failedRatio;
    private long total;


}
