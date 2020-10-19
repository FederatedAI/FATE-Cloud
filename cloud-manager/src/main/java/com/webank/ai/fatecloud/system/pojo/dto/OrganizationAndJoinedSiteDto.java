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
@ApiModel
public class OrganizationAndJoinedSiteDto implements Serializable {

    @ApiModelProperty(value = "Organization infos")
    private FederatedOrganizationDto federatedOrganizationDto;

    @ApiModelProperty(value = "total count")
    private Integer total;

    @ApiModelProperty(value = "site details")
    private List<SiteDetailDto> siteDetailDtos;
}
