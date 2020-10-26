package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@ApiModel(value = "jobs statistics for sites bean")
public class JobStatisticOfInstitutionsDimensionDto implements Serializable {

    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty(value = "successful job count for institutions")
    private String successJobCountForInstitutions;

    @ApiModelProperty(value = "failed job count for institutions")
    private String failedJobCountForInstitutions;

}
