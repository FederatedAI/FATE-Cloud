package com.webank.ai.fatecloud.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Sets;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FateSiteJobDetailDo;
import com.webank.ai.fatecloud.system.dao.entity.FateSiteJobDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FateSiteDetailMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedFateSiteMonitorMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteDto;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteItem;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorTwoSite;
import com.webank.ai.fatecloud.system.pojo.monitor.SiteBase;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FederatedFateSiteMonitorService {

    @Autowired
    FederatedFateSiteMonitorMapper federatedFateSiteMonitorMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FateSiteDetailMapper fateSiteDetailMapper;

    @Transactional
    public void pushJosStatistics(ArrayList<FateSiteJobQo> fateSiteJobQoArrayList) {
        ArrayList<FateSiteJobDo> fateSiteJobDoArrayList = new ArrayList<>();

        for (FateSiteJobQo fateSiteJobQo : fateSiteJobQoArrayList) {
            FateSiteJobDo fateSiteJobDo = new FateSiteJobDo(fateSiteJobQo);
            fateSiteJobDoArrayList.add(fateSiteJobDo);
        }

        // do not save jobs for sites that do not exist or have been deleted
        QueryWrapper<FederatedSiteManagerDo> pqw = new QueryWrapper<>();
        pqw.select("site_name", "party_id").eq("status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDoList = federatedSiteManagerMapper.selectList(pqw);
        Set<Long> partyIdSet = federatedSiteManagerDoList.stream().map(FederatedSiteManagerDo::getPartyId).collect(Collectors.toSet());
        partyIdSet.add(0L);// filter file upload site is 0

        Date date = new Date();
        for (FateSiteJobDo fateSiteJobDo : fateSiteJobDoArrayList) {

            String roles = fateSiteJobDo.getRoles();
            JSONObject rolesObject = JSON.parseObject(roles);
            try {
                HashSet<Long> newHashSet = Sets.newHashSet();
                rolesObject.forEach((key, value) -> newHashSet.addAll(JSON.parseArray(value.toString(), Long.TYPE)));
                if (!partyIdSet.containsAll(newHashSet)) {
                    log.error("the job of the site that does not exist or has been deleted will be deleted, partyIds: {}, jobId: {}", newHashSet, fateSiteJobDo.getJobId());
                    continue;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            //find the job info in t_fate_site_job table whether exist or not. if exist,update! if not,insert!
            QueryWrapper<FateSiteJobDo> fateSiteJobDoQueryWrapper = new QueryWrapper<>();
            String jobId = fateSiteJobDo.getJobId();
            fateSiteJobDoQueryWrapper.eq("party_id", fateSiteJobDo.getPartyId()).eq("role", fateSiteJobDo.getRole()).eq("job_id", jobId);

            Integer count = federatedFateSiteMonitorMapper.selectCount(fateSiteJobDoQueryWrapper);
            if (count > 0) {
                federatedFateSiteMonitorMapper.update(fateSiteJobDo, fateSiteJobDoQueryWrapper);
            } else {
                federatedFateSiteMonitorMapper.insert(fateSiteJobDo);
            }

            //find the job info in t_fate_site_job_detail table whether exist or not. if exist,update! if not,insert!
            String status = fateSiteJobDo.getStatus();
            String jobType = fateSiteJobDo.getJobType();
            Date createDate = fateSiteJobDo.getJobCreateDayDate();

            Set<Map.Entry<String, Object>> entries = rolesObject.entrySet();

            for (Map.Entry<String, Object> entry : entries) {
                String role = entry.getKey();
                Object value = entry.getValue();
                JSONArray partyIdList = JSON.parseArray(value.toString());
                for (Object o : partyIdList) {
                    long partyId = Long.parseLong(o.toString());
                    if ("local".equals(role)) {
                        partyId = fateSiteJobDo.getPartyId();
                    }
                    //build FederatedSiteManagerDo
                    String siteName = "";
                    String institutions = "";
                    QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoEw = new QueryWrapper<>();
                    federatedSiteManagerDoEw.select("site_name", "institutions").eq("party_id", partyId).ne("status", 3);
                    List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoEw);
                    if (federatedSiteManagerDos.size() > 0) {
                        siteName = federatedSiteManagerDos.get(0).getSiteName();
                        institutions = federatedSiteManagerDos.get(0).getInstitutions();
                    }

                    FateSiteJobDetailDo fateSiteJobDetailDo = new FateSiteJobDetailDo();
                    fateSiteJobDetailDo.setDetailJobId(jobId);
                    fateSiteJobDetailDo.setDetailInstitutions(institutions);
                    fateSiteJobDetailDo.setDetailSiteName(siteName);
                    fateSiteJobDetailDo.setDetailPartyId(partyId);
                    fateSiteJobDetailDo.setDetailRole(role);
                    fateSiteJobDetailDo.setDetailStatus(status);
                    fateSiteJobDetailDo.setDetailUpdateTime(date);
                    fateSiteJobDetailDo.setDetailJobCreateDayDate(createDate);
                    fateSiteJobDetailDo.setDetailJobType(jobType);

                    //handle t_fate_site_job_detail, insert or update
                    QueryWrapper<FateSiteJobDetailDo> fateSiteJobDetailDoEw = new QueryWrapper<>();
                    fateSiteJobDetailDoEw.eq("detail_job_id", jobId).eq("detail_party_id", partyId).eq("detail_role", role);
                    List<FateSiteJobDetailDo> fateSiteJobDetailDos = fateSiteDetailMapper.selectList(fateSiteJobDetailDoEw);
                    if (fateSiteJobDetailDos.size() > 0) {//update
                        fateSiteDetailMapper.update(fateSiteJobDetailDo, fateSiteJobDetailDoEw);
                    } else {//insert
                        fateSiteJobDetailDo.setDetailCreateTime(date);
                        fateSiteDetailMapper.insert(fateSiteJobDetailDo);
                    }

                }

            }

        }
    }


    public CommonResponse<FinishedJosSummaryDto> findFinishedJobsSummary(FinishedJobsSummaryQo finishedJobsSummaryQo) throws ParseException {

        String institutions = finishedJobsSummaryQo.getInstitutions();
        String site = finishedJobsSummaryQo.getSite();
        Date beginDate = finishedJobsSummaryQo.getBeginDate();
        Date endDate = finishedJobsSummaryQo.getEndDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bd = sdf.format(beginDate);
        beginDate = sdf.parse(bd);
        String ed = sdf.format(endDate);
        endDate = sdf.parse(ed);

        FinishedJosSummaryDto finishedJosSummaryDto = new FinishedJosSummaryDto();

        //get party id of site
//        Long partyId = null;
//        if (StringUtils.isNoneEmpty(site)) {
//            QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
//            federatedSiteManagerDoQueryWrapper.select("party_id").eq("site_name", site).orderByDesc("create_time");
//            List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
//            if (federatedSiteManagerDos.size() <= 0) {
//                return new CommonResponse<>(ReturnCodeEnum.SUCCESS, finishedJosSummaryDto);
//            } else {
//                partyId = federatedSiteManagerDos.get(0).getPartyId();
//            }
//        }

        ArrayList<JobTypeStatisticsBean> jobTypeStatisticsBeans = new ArrayList<>();

        //get total job statistics
        JobTypeStatisticsBean intersect = this.getJobTypeStatistics(institutions, site, "intersect", beginDate, endDate);
        JobTypeStatisticsBean modeling = this.getJobTypeStatistics(institutions, site, "modeling", beginDate, endDate);
        JobTypeStatisticsBean upload = this.getJobTypeStatistics(institutions, site, "upload", beginDate, endDate);
        JobTypeStatisticsBean download = this.getJobTypeStatistics(institutions, site, "download", beginDate, endDate);
        jobTypeStatisticsBeans.add(intersect);
        jobTypeStatisticsBeans.add(modeling);
        jobTypeStatisticsBeans.add(upload);
        jobTypeStatisticsBeans.add(download);
        long successfulJobsIntersect = intersect.getSuccessfulJobs();
        long successfulJobsModeling = modeling.getSuccessfulJobs();
        long successfulJobsUpload = upload.getSuccessfulJobs();
        long successfulJobsDownload = download.getSuccessfulJobs();
        long failedJobsIntersect = intersect.getFailedJobs();
        long failedJobsModeling = modeling.getFailedJobs();
        long failedJobsUpload = upload.getFailedJobs();
        long failedJobsDownload = download.getFailedJobs();
        finishedJosSummaryDto.setJobTypeStatisticsBeans(jobTypeStatisticsBeans);
        finishedJosSummaryDto.setSuccessfulJobs(successfulJobsIntersect + successfulJobsModeling + successfulJobsUpload + successfulJobsDownload);
        finishedJosSummaryDto.setFailedJobs(failedJobsIntersect + failedJobsModeling + failedJobsUpload + failedJobsDownload);
        finishedJosSummaryDto.setTotal(finishedJosSummaryDto.getSuccessfulJobs() + finishedJosSummaryDto.getFailedJobs());

        if (finishedJosSummaryDto.getTotal() > 0) {
            finishedJosSummaryDto.setSuccessfulRatio(String.format("%.2f", (100 * finishedJosSummaryDto.getSuccessfulJobs() / (double) finishedJosSummaryDto.getTotal())) + "%");
            finishedJosSummaryDto.setFailedRatio(String.format("%.2f", (100 * finishedJosSummaryDto.getFailedJobs() / (double) finishedJosSummaryDto.getTotal())) + "%");

        } else {
            finishedJosSummaryDto.setSuccessfulRatio("0.00%");
            finishedJosSummaryDto.setFailedRatio("0.00%");
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, finishedJosSummaryDto);
    }


    private JobTypeStatisticsBean getJobTypeStatistics(String institutions, String siteName, String type, Date beginDate, Date endDate) {
        QueryWrapper<FateSiteJobDo> ewForSuccess = new QueryWrapper<>();
        ewForSuccess.select("job_id")
                .eq(StringUtils.isNoneEmpty(institutions), "institutions", institutions).eq(StringUtils.isNoneEmpty(siteName), "site_name", siteName).between("job_create_day_date", beginDate, endDate)
                .eq("status", "success").eq("job_type", type).groupBy("job_id");
        List<FateSiteJobDo> successJobs = federatedFateSiteMonitorMapper.selectList(ewForSuccess);
        int successJobsSize = successJobs.size();

        QueryWrapper<FateSiteJobDo> ewForFailed = new QueryWrapper<>();
        ewForFailed.select("job_id")
                .eq(StringUtils.isNoneBlank(institutions), "institutions", institutions).eq(StringUtils.isNoneEmpty(siteName), "site_name", siteName).between("job_create_day_date", beginDate, endDate)
                .eq("status", "failed").eq("job_type", type).groupBy("job_id");
        List<FateSiteJobDo> failedJobs = federatedFateSiteMonitorMapper.selectList(ewForFailed);
        int failedJobsSize = failedJobs.size();

        JobTypeStatisticsBean jobTypeStatisticsBean = new JobTypeStatisticsBean();
        jobTypeStatisticsBean.setType(type);
        jobTypeStatisticsBean.setSuccessfulJobs(successJobsSize);
        jobTypeStatisticsBean.setFailedJobs(failedJobsSize);
        jobTypeStatisticsBean.setTotal(successJobsSize + failedJobsSize);
        if (jobTypeStatisticsBean.getTotal() > 0) {
            jobTypeStatisticsBean.setSuccessfulRatio(String.format("%.2f", (100 * jobTypeStatisticsBean.getSuccessfulJobs() / (double) jobTypeStatisticsBean.getTotal())) + "%");
            jobTypeStatisticsBean.setFailedRatio(String.format("%.2f", (100 * jobTypeStatisticsBean.getFailedJobs() / (double) jobTypeStatisticsBean.getTotal())) + "%");

        } else {
            jobTypeStatisticsBean.setSuccessfulRatio("0.00%");
            jobTypeStatisticsBean.setFailedRatio("0.00%");
        }

        //get jobs durations
        JobCostTimeDto jobCostTimeDto = federatedFateSiteMonitorMapper.findTimeStatistics(institutions, siteName, type, beginDate, endDate);
        if (jobCostTimeDto != null) {
            jobTypeStatisticsBean.setMinDuration(jobCostTimeDto.getMinDuration());
            jobTypeStatisticsBean.setMaxDuration(jobCostTimeDto.getMaxDuration());
            jobTypeStatisticsBean.setAvgDuration(jobCostTimeDto.getAvgDuration());
        } else {
            jobTypeStatisticsBean.setMinDuration(0L);
            jobTypeStatisticsBean.setMaxDuration(0L);
            jobTypeStatisticsBean.setAvgDuration(0L);
        }
        return jobTypeStatisticsBean;
    }


    public CommonResponse<List<JobTypedTableDto>> findTypedTable(JobTypedTableQo jobTypedTableQo) throws ParseException {
        String institutions = jobTypedTableQo.getInstitutions();
        String site = jobTypedTableQo.getSite();
        String type = jobTypedTableQo.getType();
        Date beginDate = jobTypedTableQo.getBeginDate();
        Date endDate = jobTypedTableQo.getEndDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bd = sdf.format(beginDate);
        beginDate = sdf.parse(bd);
        String ed = sdf.format(endDate);
        endDate = sdf.parse(ed);

        //get party id of site
        LinkedList<JobTypedTableDto> jobTypedTableDtos;
//        Long partyId = null;
//        if (StringUtils.isNoneEmpty(site)) {
//            QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
//            federatedSiteManagerDoQueryWrapper.select("party_id").eq("site_name", site).orderByDesc("create_time");
//            List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
//            if (federatedSiteManagerDos.size() <= 0) {
//                return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobTypedTableDtos);
//            } else {
//                partyId = federatedSiteManagerDos.get(0).getPartyId();
//            }
//        }

        jobTypedTableDtos = federatedFateSiteMonitorMapper.findTypedTable(institutions, site, type, beginDate, endDate);
        for (JobTypedTableDto jobTypedTableDto : jobTypedTableDtos) {
            Long total = jobTypedTableDto.getTotal();
            Long failedCount = jobTypedTableDto.getFailedCount();
            Long successCount = jobTypedTableDto.getSuccessCount();
            if (total > 0) {
                jobTypedTableDto.setFailedRatio(String.format("%.2f", (100 * failedCount / (double) total)) + "%");
                jobTypedTableDto.setSuccessRatio(String.format("%.2f", (100 * successCount / (double) total)) + "%");

            } else {
                jobTypedTableDto.setFailedRatio("0.00%");
            }
        }

        // add empty data for the time period without tasks
        typedTableSupplement(beginDate, endDate, sdf, jobTypedTableDtos);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobTypedTableDtos);
    }

    private void typedTableSupplement(Date beginDate, Date endDate, SimpleDateFormat sdf, LinkedList<JobTypedTableDto> jobTypedTableDtoList) {
        Set<String> dateSet = jobTypedTableDtoList.stream().map(jobTypedTableDto -> sdf.format(jobTypedTableDto.getDate())).collect(Collectors.toSet());
        Set<Date> supplementSet = Sets.newHashSet();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);

        while (beginDate.getTime() <= endDate.getTime()) {
            if (!dateSet.contains(sdf.format(beginDate))) {
                supplementSet.add(beginDate);
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            beginDate = calendar.getTime();
        }

        for (Date date : supplementSet) {
            long zero = 0;
            String zeroRatio = "0.00%";
            JobTypedTableDto jobTypedTableDto = new JobTypedTableDto(date, zero, zero, zeroRatio, zero, zeroRatio, zero, zero, zero);
            jobTypedTableDtoList.add(jobTypedTableDto);
        }
        jobTypedTableDtoList.sort(Comparator.comparing(JobTypedTableDto::getDate));
    }

    public CommonResponse<List<JobTypedDurationDto>> findTypedDuration(JobTypedTableQo jobTypedTableQo) throws ParseException {

        String institutions = jobTypedTableQo.getInstitutions();
        String site = jobTypedTableQo.getSite();
        String type = jobTypedTableQo.getType();
        Date beginDate = jobTypedTableQo.getBeginDate();
        Date endDate = jobTypedTableQo.getEndDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bd = sdf.format(beginDate);
        beginDate = sdf.parse(bd);
        String ed = sdf.format(endDate);
        endDate = sdf.parse(ed);

        //get party id of site
        LinkedList<JobTypedDurationDto> jobTypedDurationDtos = new LinkedList<>();
//        Long partyId = null;
//        if (StringUtils.isNoneEmpty(site)) {
//            QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
//            federatedSiteManagerDoQueryWrapper.select("party_id").eq("site_name", site).orderByDesc("create_time");
//            List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
//            if (federatedSiteManagerDos.size() <= 0) {
//                return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobTypedDurationDtos);
//            } else {
//                partyId = federatedSiteManagerDos.get(0).getPartyId();
//            }
//        }

        JobTypedDurationDto jobTypedDurationDto1Min = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 0L, 60 * 1000L);
        jobTypedDurationDto1Min.setDuration("<=1min");

        JobTypedDurationDto jobTypedDurationDto30Min = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 60 * 1000L, 30 * 60 * 1000L);
        jobTypedDurationDto30Min.setDuration("1-30min");

        JobTypedDurationDto jobTypedDurationDto2h = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 30 * 60 * 1000L, 2 * 60 * 60 * 1000L);
        jobTypedDurationDto2h.setDuration("30min-2h");

        JobTypedDurationDto jobTypedDurationDto6h = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 2 * 60 * 60 * 1000L, 6 * 60 * 60 * 1000L);
        jobTypedDurationDto6h.setDuration("2h-6h");

        JobTypedDurationDto jobTypedDurationDto12h = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 6 * 60 * 60 * 1000L, 12 * 60 * 60 * 1000L);
        jobTypedDurationDto12h.setDuration("6h-12h");

        JobTypedDurationDto jobTypedDurationDto1d = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 12 * 60 * 60 * 1000L, 24 * 60 * 60 * 1000L);
        jobTypedDurationDto1d.setDuration("12h-1d");

        JobTypedDurationDto jobTypedDurationDto1dMore = federatedFateSiteMonitorMapper.findTypedDuration(institutions, site, type, beginDate, endDate, 24 * 60 * 60 * 1000L, null);
        jobTypedDurationDto1dMore.setDuration(">1d");

        jobTypedDurationDtos.add(jobTypedDurationDto1Min);
        jobTypedDurationDtos.add(jobTypedDurationDto30Min);
        jobTypedDurationDtos.add(jobTypedDurationDto2h);
        jobTypedDurationDtos.add(jobTypedDurationDto6h);
        jobTypedDurationDtos.add(jobTypedDurationDto12h);
        jobTypedDurationDtos.add(jobTypedDurationDto1d);
        jobTypedDurationDtos.add(jobTypedDurationDto1dMore);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobTypedDurationDtos);
    }

    public JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {
        List<JobTypeBean> jobStatisticsSummaryInstitutionsAllForPeriod = federatedFateSiteMonitorMapper.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);

        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummaryTodayInstitutionsAllDto = new JobStatisticsSummaryTodayInstitutionsAllDto();
        for (JobTypeBean jobTypeBean : jobStatisticsSummaryInstitutionsAllForPeriod) {
            String status = jobTypeBean.getStatus();
            Integer count = jobTypeBean.getCount();
            if ("success".equals(status)) {
                jobStatisticsSummaryTodayInstitutionsAllDto.setSuccessJobCount(count);
            }
            if ("failed".equals(status)) {
                jobStatisticsSummaryTodayInstitutionsAllDto.setFailedJobCount(count);
            }
            if ("running".equals(status)) {
                jobStatisticsSummaryTodayInstitutionsAllDto.setRunningJobCount(count);
            }
            if ("waiting".equals(status)) {
                jobStatisticsSummaryTodayInstitutionsAllDto.setWaitingJobCount(count);
            }
        }
        Integer failedJobCount = jobStatisticsSummaryTodayInstitutionsAllDto.getFailedJobCount();
        if (failedJobCount == null) {
            jobStatisticsSummaryTodayInstitutionsAllDto.setFailedJobCount(0);
        }
        Integer runningJobCount = jobStatisticsSummaryTodayInstitutionsAllDto.getRunningJobCount();
        if (runningJobCount == null) {
            jobStatisticsSummaryTodayInstitutionsAllDto.setRunningJobCount(0);
        }
        Integer successJobCount = jobStatisticsSummaryTodayInstitutionsAllDto.getSuccessJobCount();
        if (successJobCount == null) {
            jobStatisticsSummaryTodayInstitutionsAllDto.setSuccessJobCount(0);
        }
        Integer waitingJobCount = jobStatisticsSummaryTodayInstitutionsAllDto.getWaitingJobCount();
        if (waitingJobCount == null) {
            jobStatisticsSummaryTodayInstitutionsAllDto.setWaitingJobCount(0);
        }

        jobStatisticsSummaryTodayInstitutionsAllDto.setInstitutionsCount(jobStatisticsSummaryTodayInstitutionsAllDto.getSuccessJobCount() + jobStatisticsSummaryTodayInstitutionsAllDto.getFailedJobCount() + jobStatisticsSummaryTodayInstitutionsAllDto.getWaitingJobCount() + jobStatisticsSummaryTodayInstitutionsAllDto.getRunningJobCount());
        return jobStatisticsSummaryTodayInstitutionsAllDto;
    }


    public PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> getJobStatisticsSummaryInstitutionsEachForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {

        List<String> activatedInstitutionsPeriod = federatedFateSiteMonitorMapper.getActivatedInstitutionsPeriod(jobStatisticsSummaryForPeriodQo);
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtoPageBean = new PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>(jobStatisticsSummaryForPeriodQo.getPageNum(), jobStatisticsSummaryForPeriodQo.getPageSize(), activatedInstitutionsPeriod.size());

        long startIndex = jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtos = federatedFateSiteMonitorMapper.getInstitutionPeriod(startIndex, jobStatisticsSummaryForPeriodQo);
        jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.setList(jobStatisticsSummaryTodayInstitutionsEachDtos);
        return jobStatisticsSummaryTodayInstitutionsEachDtoPageBean;

    }

    public JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedFateSiteMonitorMapper.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);

        return jobStatisticsSummaryTodaySiteAllDto;

    }

    public PageBean<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummarySiteEachForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        long count = federatedFateSiteMonitorMapper.getJobStatisticsSummarySiteEachForPeriodCount(jobStatisticsSummarySiteAllForPeriodQo);
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtoPageBean = new PageBean<>(jobStatisticsSummarySiteAllForPeriodQo.getPageNum(), jobStatisticsSummarySiteAllForPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsSummaryTodaySiteEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedFateSiteMonitorMapper.getJobStatisticsSummarySiteEachForPeriod(startIndex, jobStatisticsSummarySiteAllForPeriodQo);

        jobStatisticsSummaryTodaySiteEachDtoPageBean.setList(jobStatisticsSummaryTodaySiteEachDtos);
        return jobStatisticsSummaryTodaySiteEachDtoPageBean;

    }

    public MonitorSiteDto getJobStatisticsOfSiteDimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) throws ParseException {
        String institutions = jobOfSiteDimensionPeriodQo.getInstitutions();
        Date beginDate = jobOfSiteDimensionPeriodQo.getBeginDate();
        Date endDate = jobOfSiteDimensionPeriodQo.getEndDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bd = sdf.format(beginDate);
        beginDate = sdf.parse(bd);
        String ed = sdf.format(endDate);
        endDate = sdf.parse(ed);

        //get table site columns
        QueryWrapper<FateSiteJobDetailDo> fateSiteJobDetailDoEw = new QueryWrapper<>();
        fateSiteJobDetailDoEw.select("detail_site_name").eq("detail_institutions", institutions).between("detail_job_create_day_date", bd, ed).groupBy("detail_site_name").orderByAsc("detail_site_name");
        List<FateSiteJobDetailDo> fateSiteJobDetailDos = fateSiteDetailMapper.selectList(fateSiteJobDetailDoEw);
        //HashSet<String> sites = new HashSet<>();
        TreeSet<String> sites = new TreeSet<>();// sort 2021-06-21
        for (FateSiteJobDetailDo fateSiteJobDetailDo : fateSiteJobDetailDos) {
            String detailSiteName = fateSiteJobDetailDo.getDetailSiteName();
            sites.add(detailSiteName);
        }

        //get paged job statistics
        int count = federatedFateSiteMonitorMapper.findCountOfSitePeriod(jobOfSiteDimensionPeriodQo);
        PageBean<MonitorSiteItem> jobStatisticsOfSiteDimensionPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsOfSiteDimensionPageBean.getStartIndex();
        List<MonitorSiteItem> siteItems = federatedFateSiteMonitorMapper.getPagedJobStatisticsOfSiteDimensionForPeriodDynamicRow(startIndex, jobOfSiteDimensionPeriodQo);
        for (MonitorSiteItem siteItem : siteItems) { // institutional collection
            List<MonitorTwoSite> institutionSiteList = siteItem.getInstitutionSiteList();
            for (MonitorTwoSite monitorTwoSite : institutionSiteList) { // collection of sites under the organization
                List<SiteBase> mixSiteModeling = monitorTwoSite.getMixSiteModeling();
                for (SiteBase siteBase : mixSiteModeling) { // site details
                    Integer totalJobs = siteBase.getTotalJobs();
                    Integer successJobs = siteBase.getSuccessJobs();
                    Integer runningJobs = siteBase.getRunningJobs();
                    Integer failedJobs = siteBase.getFailedJobs();
                    Integer waitingJobs = siteBase.getWaitingJobs();
                    if (totalJobs > 0) {
                        siteBase.setSuccessPercent(String.format("%.2f", (100 * successJobs / (double) totalJobs)) + "%");
                        siteBase.setRunningPercent(String.format("%.2f", (100 * runningJobs / (double) totalJobs)) + "%");
                        siteBase.setFailedPercent(String.format("%.2f", (100 * failedJobs / (double) totalJobs)) + "%");
                        siteBase.setWaitingPercent(String.format("%.2f", (100 * waitingJobs / (double) totalJobs)) + "%");
                    } else {
                        siteBase.setSuccessPercent("0.00%");
                        siteBase.setRunningPercent("0.00%");
                        siteBase.setFailedPercent("0.00%");
                        siteBase.setWaitingPercent("0.00%");
                    }
                }
            }
        }

        // modify your own site modeling display to be unique 2021-06-18
        /*HashSet<String> match = Sets.newHashSet();
        for (MonitorSiteItem siteItem : siteItems) {
            if (institutions.equals(siteItem.getInstitution())) {
                List<MonitorTwoSite> institutionSiteList = siteItem.getInstitutionSiteList();
                for (MonitorTwoSite monitorTwoSite : institutionSiteList) {
                    String institutionSiteName = monitorTwoSite.getInstitutionSiteName();
                    List<SiteBase> mixSiteModeling = monitorTwoSite.getMixSiteModeling();
                    for (SiteBase siteBase : mixSiteModeling) {
                        String siteBaseName = siteBase.getSiteName();
                        String combination = institutionSiteName + siteBaseName;
                        if (match.contains(combination)) {
                            siteBase.setSuccessPercent("0.00%");
                            siteBase.setRunningPercent("0.00%");
                            siteBase.setFailedPercent("0.00%");
                            siteBase.setWaitingPercent("0.00%");
                            siteBase.setTotalJobs(0);
                        } else {
                            match.add(institutionSiteName + siteBaseName);
                            match.add(siteBaseName + institutionSiteName);
                        }
                    }
                }
            }
        }*/

        MonitorSiteDto monitorSiteDto = new MonitorSiteDto();
        monitorSiteDto.setSiteList(new ArrayList<>(sites));
        monitorSiteDto.setTotal(count);
        monitorSiteDto.setData(siteItems);

        return monitorSiteDto;
    }


    public PageBean<JobStatisticOfInstitutionsDimensionDto> getJobStatisticsODimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {
        int count = federatedFateSiteMonitorMapper.findInstitutionsCountPeriod(jobOfSiteDimensionPeriodQo);

        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtoPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticOfInstitutionsDimensionDtoPageBean.getStartIndex();

        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtos = federatedFateSiteMonitorMapper.getPagedJobStatisticsODimensionForPeriod(startIndex, jobOfSiteDimensionPeriodQo);

        for (int i = 0; i < jobStatisticOfInstitutionsDimensionDtos.size(); i++) {
            if (jobOfSiteDimensionPeriodQo.getInstitutions().equals(jobStatisticOfInstitutionsDimensionDtos.get(i).getInstitutions())) {//replace the data if result contains the input institutions
                JobStatisticOfInstitutionsDimensionDto inputInstitutions = federatedFateSiteMonitorMapper.getInputInsitutionsStatistics(jobOfSiteDimensionPeriodQo);
                inputInstitutions.setInstitutions(jobOfSiteDimensionPeriodQo.getInstitutions());
                jobStatisticOfInstitutionsDimensionDtos.set(i, inputInstitutions);
            }
        }

        jobStatisticOfInstitutionsDimensionDtoPageBean.setList(jobStatisticOfInstitutionsDimensionDtos);
        return jobStatisticOfInstitutionsDimensionDtoPageBean;
    }

    public List<String> getSiteAll(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        LinkedList<String> siteList = new LinkedList<>();
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.select("site_name").eq("institutions", authorityApplyDetailsQo.getInstitutions()).orderByAsc("site_name");
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            siteList.add(federatedSiteManagerDo.getSiteName());
        }
        return siteList;
    }
}
