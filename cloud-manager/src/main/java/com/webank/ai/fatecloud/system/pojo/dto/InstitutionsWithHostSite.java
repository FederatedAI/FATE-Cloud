package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "the institutions with host information")
public class InstitutionsWithHostSite implements Serializable {

    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty(value = "institutions with host site information")
    private List<JobStatistics> jobStatisticsList;

}
