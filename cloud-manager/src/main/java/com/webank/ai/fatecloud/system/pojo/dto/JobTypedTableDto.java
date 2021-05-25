package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "dto for typed jobs table")
public class JobTypedTableDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date date;
    private Long total;
    private Long failedCount;
    private String failedRatio;
    private Long successCount;
    private String successRatio;
    private Long avgDuration;
    private Long minDuration;
    private Long maxDuration;
}
