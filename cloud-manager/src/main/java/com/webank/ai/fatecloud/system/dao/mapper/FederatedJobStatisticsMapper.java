/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FederatedJobStatisticsMapper extends BaseMapper<FederatedJobStatisticsDo> {

    JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryTodayInstitutionsAll(JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo);

    JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummaryTodaySiteAll(JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo);

    long getJobStatisticsSummaryTodaySiteEachCount(JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo);

    List<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummaryTodaySiteEach(@Param(value = "startIndex") long startIndex, @Param(value = "jobStatisticsSummaryTodaySiteAllQo") JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo);

    JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    long getJobStatisticsSummarySiteEachForPeriodCount(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    List<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummarySiteEachForPeriod(@Param(value = "startIndex") long startIndex, @Param(value = "jobStatisticsSummarySiteAllForPeriodQo") JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    List<JobStatisticOfInstitutionsDimensionDto> getPagedJobStatisticsODimension(@Param(value = "startIndex") long startIndex, @Param(value = "jobOfSiteDimensionQo") JobOfSiteDimensionQo jobOfSiteDimensionQo);

    List<JobStatisticOfInstitutionsDimensionDto> getPagedJobStatisticsODimensionForPeriod(@Param(value = "startIndex") long startIndex, @Param(value = "jobOfSiteDimensionPeriodQo") JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    long findCountOfSite(JobOfSiteDimensionQo jobOfSiteDimensionQo);

    List<JobStatisticsOfSiteDimension> getPagedJobStatisticsOfSiteDimensionDynamicRow(@Param(value = "startIndex") long startIndex, @Param(value = "jobOfSiteDimensionQo") JobOfSiteDimensionQo jobOfSiteDimensionQo);

    List<InstitutionsWithSites> findInstitutionsWithSitesPaged(@Param("startIndex") long startIndex, @Param("jobOfSiteDimensionQo") JobOfSiteDimensionQo jobOfSiteDimensionQo);

    long findCountOfSitePeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<JobStatisticsOfSiteDimension> getPagedJobStatisticsOfSiteDimensionForPeriodDynamicRow(@Param(value = "startIndex") long startIndex, @Param(value = "jobOfSiteDimensionPeriodQo") JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<InstitutionsWithSites> findInstitutionsWithSitesPagedPeriod(@Param("startIndex") long startIndex, @Param("jobOfSiteDimensionPeriodQo") JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<String> getActivatedInstitutionsToday(JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo);

    List<JobStatisticsSummaryTodayInstitutionsEachDto> getInstitutionToday(@Param("startIndex") long startIndex, @Param("jobStatisticsSummaryTodayQo") JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo);

    List<String> getActivatedInstitutionsPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    List<JobStatisticsSummaryTodayInstitutionsEachDto> getInstitutionPeriod(@Param("startIndex") long startIndex, @Param("jobStatisticsSummaryForPeriodQo") JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    int findInstitutionsCountToday(JobOfSiteDimensionQo jobOfSiteDimensionQo);

    int findInstitutionsCountPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);
}
