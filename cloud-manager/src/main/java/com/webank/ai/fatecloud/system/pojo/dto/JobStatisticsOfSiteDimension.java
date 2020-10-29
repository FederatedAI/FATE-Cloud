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
@ApiModel(value = "jobs statistics for sites")
public class JobStatisticsOfSiteDimension implements Serializable {

    @ApiModelProperty(value = "guest name")
    private String siteGuestName;

    @ApiModelProperty(value = "institutions with host site information")
    private List<InstitutionsWithHostSite> institutionsWithHostSites;

}
