package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "dto for finished jobs summary")
public class FinishedJosSummaryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long total;
    private Long successfulJobs;
    private String successfulRatio;
    private Long failedJobs;
    private String failedRatio;

    private List<JobTypeStatisticsBean> jobTypeStatisticsBeans;
}
