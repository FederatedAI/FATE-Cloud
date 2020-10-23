package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@ApiModel(value = "jobs statistics for sites bean")
public class JobStatisticsOfSiteDimensionDto implements Serializable {

    @ApiModelProperty(value = "statistics for job")
    private List<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensions;

    @ApiModelProperty(value = "site list of the institutions")
    private List<String> sites;

    @ApiModelProperty(value = "site list of the institutions")
    private List<InstitutionsWithSites> institutionsWithSites;
}
