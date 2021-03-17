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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    String BaseSQL = "SUM(job_success_count + job_running_count + job_failed_count + job_waiting_count ) AS totalJobs," +
            "SUM(job_success_count) AS successJobs," +
            "SUM(job_running_count) AS runningJobs," +
            "SUM(job_failed_count) AS failedJobs," +
            "SUM(job_waiting_count) AS waitingJobs," +
            "SUM(job_success_count) / SUM(job_success_count + job_running_count + job_failed_count + job_waiting_count ) AS successPercent," +
            "SUM(job_running_count) / SUM(job_success_count + job_running_count + job_failed_count + job_waiting_count ) AS runningPercent," +
            "SUM(job_failed_count) / SUM(job_success_count + job_running_count + job_failed_count + job_waiting_count ) AS failedPercent," +
            "SUM(job_waiting_count) / SUM(job_success_count + job_running_count + job_failed_count + job_waiting_count ) AS waitingPercent " +
            "FROM t_job_statistics " +
            "WHERE create_time between #{startDate} and #{endDate} ";

    @Select("SELECT " + BaseSQL)
    List<Base> getTotal(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT " +
            "site_guest_institutions As guestInstitution, " +
            "site_host_institutions  As hostInstitution," +
            BaseSQL +
            " group by guestInstitution, hostInstitution")
    List<TwoInstitutionBase> getTotalDetail(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT " +
            "IF(site_guest_institutions=#{insitution},site_guest_name,site_host_name) AS siteName," +
            BaseSQL + " and " +
            "(site_guest_institutions=#{insitution} or site_host_institutions =#{insitution}) " +
            " group by siteName")
    List<SiteBase> getInsitutionDetail(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("insitution") String insitution);

    @Select("SELECT " +
            "  IF(site_guest_institutions =#{insitution},site_host_institutions,site_guest_institutions) AS institution," +
            BaseSQL + " and " +
            "  (site_guest_institutions = #{insitution} OR site_host_institutions =#{insitution}) AND " +
            "  (site_guest_institutions != site_host_institutions)" +
            " GROUP BY institution" +
            " UNION ALL " +
            " SELECT " +
            "  site_guest_institutions AS institution," + BaseSQL + " and " +
            "  site_guest_institutions = #{insitution} AND " +
            "  (site_guest_institutions = site_host_institutions)" +
            " GROUP BY institution")
    List<InstitutionBase> getMonitorInstitution(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("insitution") String insitution);

    @Select("SELECT " +
            "  IF(site_guest_institutions =#{insitution},site_host_institutions,site_guest_institutions) AS institution," +
            "  IF(site_guest_institutions =#{insitution},site_host_name,site_guest_name) AS institutionSiteName,        " +
            "  IF(site_guest_institutions =#{insitution},site_guest_name,site_host_name) AS siteName," +
            BaseSQL + " and " +
            "  (site_guest_institutions = #{insitution} OR site_host_institutions =#{insitution}) AND " +
            "  (site_guest_institutions != site_host_institutions)" +
            " GROUP BY institution,institutionSiteName,siteName" +
            " UNION ALL " +
            " SELECT " +
            "  site_host_institutions AS institution," +
            "  site_guest_name AS institutionSiteName, " +
            "  site_host_name AS siteName," +
            BaseSQL + " and " +
            "  site_guest_institutions = #{insitution} AND " +
            "  (site_guest_institutions = site_host_institutions)" +
            " GROUP BY  institution,institutionSiteName,siteName")
    List<TwoSiteBase> getMonitorSite(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("insitution") String insitution);
}
