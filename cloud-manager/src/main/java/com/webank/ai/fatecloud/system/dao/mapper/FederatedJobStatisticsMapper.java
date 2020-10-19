package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticsOfSiteDimensionDto;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionQo;

import java.util.List;

public interface FederatedJobStatisticsMapper extends BaseMapper<FederatedJobStatisticsDo> {

    List<JobStatisticsOfSiteDimensionDto> getJobStatisticsOfSiteDimension(JobOfSiteDimensionQo jobOfSiteDimensionQo);

}
