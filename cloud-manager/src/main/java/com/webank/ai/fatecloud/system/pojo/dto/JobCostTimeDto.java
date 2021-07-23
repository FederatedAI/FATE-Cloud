package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job cost time dto")
public class JobCostTimeDto implements Serializable {
    private Long avgDuration;
    private Long minDuration;
    private Long maxDuration;

}
