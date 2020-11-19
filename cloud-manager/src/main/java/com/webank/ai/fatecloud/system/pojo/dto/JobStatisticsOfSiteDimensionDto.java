package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.common.util.PageBean;
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
public class JobStatisticsOfSiteDimensionDto implements Serializable {

    private PageBean<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensions;

    private List<String> sites;

    private List<InstitutionsWithSites> institutionsWithSites;
}
