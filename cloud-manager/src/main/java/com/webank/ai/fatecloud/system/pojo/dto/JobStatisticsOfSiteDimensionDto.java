package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "jobs statistics for sites")
public class JobStatisticsOfSiteDimensionDto implements Serializable {

    @ApiModelProperty(value = "guest id")
    private String siteGuestId;

    @ApiModelProperty(value = "institutions with host site information")
    private List<InstitutionsWithHostSite> institutionsWithHostSites;

}
