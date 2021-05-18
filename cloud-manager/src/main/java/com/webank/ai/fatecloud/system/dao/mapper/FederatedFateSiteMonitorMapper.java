package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FateSiteJobDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteItem;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionPeriodQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsSummaryForPeriodQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsSummarySiteAllForPeriodQo;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public interface FederatedFateSiteMonitorMapper extends BaseMapper<FateSiteJobDo> {
    JobCostTimeDto findTimeStatistics(String institutions , String siteName, String type, Date beginDate, Date endDate);

    LinkedList<JobTypedTableDto> findTypedTable(String institutions, String siteName, String type, Date beginDate, Date endDate);

    JobTypedDurationDto findTypedDuration(String institutions, String siteName, String type, Date beginDate, Date endDate,Long min,Long max);

    List<JobTypeBean> getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    List<String> getActivatedInstitutionsPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    List<JobStatisticsSummaryTodayInstitutionsEachDto> getInstitutionPeriod(@Param("startIndex") long startIndex, @Param("jobStatisticsSummaryForPeriodQo") JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo);

    JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    long getJobStatisticsSummarySiteEachForPeriodCount(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    List<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummarySiteEachForPeriod(long startIndex, JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo);

    int findCountOfSitePeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<MonitorSiteItem> getPagedJobStatisticsOfSiteDimensionForPeriodDynamicRow(long startIndex, JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    int findInstitutionsCountPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<JobStatisticOfInstitutionsDimensionDto> getPagedJobStatisticsODimensionForPeriod(long startIndex, JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    JobStatisticOfInstitutionsDimensionDto getInputInsitutionsStatistics(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);
}
