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
package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedJobStatisticsMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FederatedJobStatisticsService {

    @Autowired
    FederatedJobStatisticsMapper federatedJobStatisticsMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;


    @Transactional
    public void pushJosStatistics(List<JobStatisticsQo> jobStatisticsQos) {
        for (JobStatisticsQo jobStatisticsQo : jobStatisticsQos) {
            //find the job info whether exist or not. if exist,update! if not,insert!
            FederatedJobStatisticsDo federatedJobStatisticsDo = new FederatedJobStatisticsDo(jobStatisticsQo);

            Date date = new Date(jobStatisticsQo.getJobFinishDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(date);

            QueryWrapper<FederatedJobStatisticsDo> federatedJobStatisticsDoQueryWrapper = new QueryWrapper<>();
            federatedJobStatisticsDoQueryWrapper.eq("site_guest_id", jobStatisticsQo.getSiteGuestId()).eq("site_host_id", jobStatisticsQo.getSiteHostId()).eq("job_finish_date", format);

            Integer count = federatedJobStatisticsMapper.selectCount(federatedJobStatisticsDoQueryWrapper);
            if (count > 0) {
                federatedJobStatisticsMapper.update(federatedJobStatisticsDo, federatedJobStatisticsDoQueryWrapper);
            } else {
                //link the site information by id

                FederatedSiteManagerDo federatedSiteManagerDoGuest = federatedSiteManagerMapper.selectById(jobStatisticsQo.getSiteGuestId());
                federatedJobStatisticsDo.setSiteGuestInstitutions(federatedSiteManagerDoGuest.getInstitutions());
                federatedJobStatisticsDo.setSiteGuestName(federatedSiteManagerDoGuest.getSiteName());

                FederatedSiteManagerDo federatedSiteManagerDoHost = federatedSiteManagerMapper.selectById(jobStatisticsQo.getSiteHostId());
                federatedJobStatisticsDo.setSiteHostInstitutions(federatedSiteManagerDoHost.getInstitutions());
                federatedJobStatisticsDo.setSiteHostName(federatedSiteManagerDoHost.getSiteName());

                federatedJobStatisticsMapper.insert(federatedJobStatisticsDo);
            }
        }
    }


    public JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {
        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummaryTodayInstitutionsAllDto = federatedJobStatisticsMapper.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);
        return jobStatisticsSummaryTodayInstitutionsAllDto;
    }


    public PageBean<JobStatisticOfInstitutionsDimensionDto> getJobStatisticsODimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {

        int count = federatedJobStatisticsMapper.findInstitutionsCountPeriod(jobOfSiteDimensionPeriodQo);

        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtoPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticOfInstitutionsDimensionDtoPageBean.getStartIndex();

        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtos = federatedJobStatisticsMapper.getPagedJobStatisticsODimensionForPeriod(startIndex, jobOfSiteDimensionPeriodQo);
        jobStatisticOfInstitutionsDimensionDtoPageBean.setList(jobStatisticOfInstitutionsDimensionDtos);

        return jobStatisticOfInstitutionsDimensionDtoPageBean;
    }

    public MonitorSiteDto getJobStatisticsOfSiteDimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {

        //get table site columns
        String institutions = jobOfSiteDimensionPeriodQo.getInstitutions();
        HashSet<String> sites = new HashSet<>();
        QueryWrapper<FederatedJobStatisticsDo> federatedJobStatisticsDoQueryWrapper = new QueryWrapper<>();
        federatedJobStatisticsDoQueryWrapper.eq("site_guest_institutions", institutions).or().eq("site_host_institutions", institutions);
        List<FederatedJobStatisticsDo> federatedJobStatisticsDos = federatedJobStatisticsMapper.selectList(federatedJobStatisticsDoQueryWrapper);
        for (FederatedJobStatisticsDo federatedJobStatisticsDo : federatedJobStatisticsDos) {

            String siteGuestInstitutions = federatedJobStatisticsDo.getSiteGuestInstitutions();
            String siteHostInstitutions = federatedJobStatisticsDo.getSiteHostInstitutions();

            if (siteGuestInstitutions.equals(institutions)) {
                String siteGuestName = federatedJobStatisticsDo.getSiteGuestName();
                sites.add(siteGuestName);
            }
            if (siteHostInstitutions.equals(institutions)) {
                String siteHostName = federatedJobStatisticsDo.getSiteHostName();
                sites.add(siteHostName);
            }
        }

        //get paged job statistics
        int count = federatedJobStatisticsMapper.findCountOfSitePeriod(jobOfSiteDimensionPeriodQo);
        PageBean<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsOfSiteDimensionPageBean.getStartIndex();
        List<MonitorSiteItem> siteItems = federatedJobStatisticsMapper.getPagedJobStatisticsOfSiteDimensionForPeriodDynamicRow(startIndex, jobOfSiteDimensionPeriodQo);
        for (MonitorSiteItem siteItem : siteItems) {
            List<MonitorTwoSite> institutionSiteList = siteItem.getInstitutionSiteList();
            for (MonitorTwoSite monitorTwoSite : institutionSiteList) {
                List<SiteBase> mixSiteModeling = monitorTwoSite.getMixSiteModeling();
                for (SiteBase siteBase : mixSiteModeling) {
                    Integer totalJobs = siteBase.getTotalJobs();
                    Integer successJobs = siteBase.getSuccessJobs();
                    Integer runningJobs = siteBase.getRunningJobs();
                    Integer failedJobs = siteBase.getFailedJobs();
                    Integer waitingJobs = siteBase.getWaitingJobs();
                    if (totalJobs > 0) {
                        siteBase.setSuccessPercent(String.format("%.2f", (double) (100 * successJobs / totalJobs)) + "%");
                        siteBase.setRunningPercent(String.format("%.2f", (double) (100 * runningJobs / totalJobs)) + "%");
                        siteBase.setFailedPercent(String.format("%.2f", (double) (100 * failedJobs / totalJobs)) + "%");
                        siteBase.setWaitingPercent(String.format("%.2f", (double) (100 * waitingJobs / totalJobs)) + "%");
                    } else {
                        siteBase.setSuccessPercent("0.00%");
                        siteBase.setRunningPercent("0.00%");
                        siteBase.setFailedPercent("0.00%");
                        siteBase.setWaitingPercent("0.00%");
                    }
                }
            }
        }
        MonitorSiteDto monitorSiteDto = new MonitorSiteDto();
        ArrayList<String> siteList = new ArrayList<>(sites);
        Collections.sort(siteList);
        monitorSiteDto.setSiteList(siteList);
        monitorSiteDto.setTotal(count);
        monitorSiteDto.setData(siteItems);

        return monitorSiteDto;
    }


    public PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> getJobStatisticsSummaryInstitutionsEachForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {

        List<String> activatedInstitutionsPeriod = federatedJobStatisticsMapper.getActivatedInstitutionsPeriod(jobStatisticsSummaryForPeriodQo);
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtoPageBean = new PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>(jobStatisticsSummaryForPeriodQo.getPageNum(), jobStatisticsSummaryForPeriodQo.getPageSize(), activatedInstitutionsPeriod.size());

        long startIndex = jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtos = federatedJobStatisticsMapper.getInstitutionPeriod(startIndex, jobStatisticsSummaryForPeriodQo);
        jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.setList(jobStatisticsSummaryTodayInstitutionsEachDtos);
        return jobStatisticsSummaryTodayInstitutionsEachDtoPageBean;

    }

    public JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedJobStatisticsMapper.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);

        return jobStatisticsSummaryTodaySiteAllDto;
    }

    public PageBean<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummarySiteEachForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {

        long count = federatedJobStatisticsMapper.getJobStatisticsSummarySiteEachForPeriodCount(jobStatisticsSummarySiteAllForPeriodQo);
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtoPageBean = new PageBean<>(jobStatisticsSummarySiteAllForPeriodQo.getPageNum(), jobStatisticsSummarySiteAllForPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsSummaryTodaySiteEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedJobStatisticsMapper.getJobStatisticsSummarySiteEachForPeriod(startIndex, jobStatisticsSummarySiteAllForPeriodQo);

        jobStatisticsSummaryTodaySiteEachDtoPageBean.setList(jobStatisticsSummaryTodaySiteEachDtos);
        return jobStatisticsSummaryTodaySiteEachDtoPageBean;
    }

    //monitor write by nobslin
    public MonitorTotalDto getMonitorTotal(TotalReqQo totalReqQo) {
        MonitorTotalDto monitorTotalDto = new MonitorTotalDto();

        List<Base> totalList = federatedJobStatisticsMapper.getTotal(totalReqQo.getStartDate(), totalReqQo.getEndDate());
        if (!totalList.isEmpty()) {
            Base base = totalList.get(0);
            monitorTotalDto.setTotalJobs(base.getTotalJobs());
            monitorTotalDto.setSuccessJobs(base.getSuccessJobs());
            monitorTotalDto.setRunningJobs(base.getRunningJobs());
            monitorTotalDto.setFailedJobs(base.getFailedJobs());
            monitorTotalDto.setWaitingJobs(base.getWaitingJobs());
            if (base.getTotalJobs() > 0) {
                monitorTotalDto.setSuccessPercent(String.format("%.2f", Double.valueOf(100 * base.getSuccessJobs() / base.getTotalJobs())) + "%");
                monitorTotalDto.setRunningPercent(String.format("%.2f", Double.valueOf(100 * base.getRunningJobs() / base.getTotalJobs())) + "%");
                monitorTotalDto.setFailedPercent(String.format("%.2f", Double.valueOf(100 * base.getFailedJobs() / base.getTotalJobs())) + "%");
                monitorTotalDto.setWaitingPercent(String.format("%.2f", Double.valueOf(100 * base.getWaitingJobs() / base.getTotalJobs())) + "%");
            } else {
                monitorTotalDto.setSuccessPercent("0.00%");
                monitorTotalDto.setRunningPercent("0.00%");
                monitorTotalDto.setFailedPercent("0.00%");
                monitorTotalDto.setWaitingPercent("0.00%");
            }
        } else {
            return null;
        }
        List<TwoInstitutionBase> totalDetailList = federatedJobStatisticsMapper.getTotalDetail(totalReqQo.getStartDate(), totalReqQo.getEndDate());
        Map<String, List<TwoInstitutionBase>> map = new HashMap<>();
        for (int i = 0; i < totalDetailList.size(); i++) {
            TwoInstitutionBase item = totalDetailList.get(i);
            if (item.getGuestInstitution().equals(item.getHostInstitution())) {
                if (map.containsKey(item.getGuestInstitution())) {
                    map.get(item.getGuestInstitution()).add(item);
                } else {
                    List<TwoInstitutionBase> list = new ArrayList<>();
                    list.add(item);
                    map.put(item.getGuestInstitution(), list);
                }
            } else {
                if (map.containsKey(item.getGuestInstitution())) {
                    map.get(item.getGuestInstitution()).add(item);
                } else {
                    List<TwoInstitutionBase> list = new ArrayList<>();
                    list.add(item);
                    map.put(item.getGuestInstitution(), list);
                }
                if (map.containsKey(item.getHostInstitution())) {
                    map.get(item.getHostInstitution()).add(item);
                } else {
                    List<TwoInstitutionBase> list = new ArrayList<>();
                    list.add(item);
                    map.put(item.getHostInstitution(), list);
                }
            }
        }

        List<InstitutionBase> institutionBaseList = new ArrayList<>();
        for (Map.Entry<String, List<TwoInstitutionBase>> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            List<TwoInstitutionBase> mapValue = entry.getValue();
            InstitutionBase institutionBase = new InstitutionBase();
            Integer total = 0, success = 0, running = 0, failed = 0, waiting = 0;
            for (int i = 0; i < mapValue.size(); i++) {
                TwoInstitutionBase item = mapValue.get(i);
                total += item.getTotalJobs();
                success += item.getSuccessJobs();
                running += item.getRunningJobs();
                failed += item.getFailedJobs();
                waiting += item.getWaitingJobs();
            }
            institutionBase.setTotalJobs(total);
            institutionBase.setSuccessJobs(success);
            institutionBase.setRunningJobs(running);
            institutionBase.setFailedJobs(failed);
            institutionBase.setWaitingJobs(waiting);
            if (total > 0) {
                institutionBase.setSuccessPercent(String.format("%.2f", Double.valueOf(100 * success / total)) + "%");
                institutionBase.setRunningPercent(String.format("%.2f", Double.valueOf(100 * running / total)) + "%");
                institutionBase.setFailedPercent(String.format("%.2f", Double.valueOf(100 * failed / total)) + "%");
                institutionBase.setWaitingPercent(String.format("%.2f", Double.valueOf(100 * waiting / total)) + "%");
            } else {
                institutionBase.setSuccessPercent("0.00%");
                institutionBase.setRunningPercent("0.00%");
                institutionBase.setFailedPercent("0.00%");
                institutionBase.setWaitingPercent("0.00%");
            }
            institutionBase.setInstitution(mapKey);
            institutionBaseList.add(institutionBase);
        }
        monitorTotalDto.setData(institutionBaseList);
        return monitorTotalDto;
    }

    public InstitutionDetailDto getInsitutionDetail(InsitutionDetailQo insitutionDetailQo) {
        InstitutionDetailDto monitorTotalDto = new InstitutionDetailDto();
        List<SiteBase> siteBaseList = federatedJobStatisticsMapper.getInsitutionDetail(insitutionDetailQo.getStartDate(), insitutionDetailQo.getEndDate(), insitutionDetailQo.getInstitution());
        List<SiteBase> list = new ArrayList<>();
        for (int i = 0; i < siteBaseList.size(); i++) {
            SiteBase siteBase = siteBaseList.get(i);
            if (siteBase.getTotalJobs() > 0) {
                siteBase.setSuccessPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getSuccessJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setRunningPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getRunningJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setFailedPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getFailedJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setWaitingPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getWaitingJobs() / siteBase.getTotalJobs())) + "%");
            } else {
                siteBase.setSuccessPercent("0.00%");
                siteBase.setRunningPercent("0.00%");
                siteBase.setFailedPercent("0.00%");
                siteBase.setWaitingPercent("0.00%");
            }
            list.add(siteBase);
        }
        monitorTotalDto.setData(list);
        return monitorTotalDto;
    }

    public MonitorInstituionDto getInstitutionMonitor(MonitorInsitutionQo monitorInsitutionQo) {
        MonitorInstituionDto monitorInstituionDto = new MonitorInstituionDto();
//        Page<InstitutionBase> page = PageHelper.startPage(monitorInsitutionQo.getPageNum(), monitorInsitutionQo.getPageSize());
        List<InstitutionBase> institutionBaseList = federatedJobStatisticsMapper.getMonitorInstitution(monitorInsitutionQo.getStartDate(), monitorInsitutionQo.getEndDate(), monitorInsitutionQo.getInstitution());
        List<InstitutionBase> list = new ArrayList<>();
        for (int i = 0; i < institutionBaseList.size(); i++) {
            InstitutionBase siteBase = institutionBaseList.get(i);
            if (siteBase.getTotalJobs() > 0) {
                siteBase.setSuccessPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getSuccessJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setRunningPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getRunningJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setFailedPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getFailedJobs() / siteBase.getTotalJobs())) + "%");
                siteBase.setWaitingPercent(String.format("%.2f", Double.valueOf(100 * siteBase.getWaitingJobs() / siteBase.getTotalJobs())) + "%");
            } else {
                siteBase.setSuccessPercent("0.00%");
                siteBase.setRunningPercent("0.00%");
                siteBase.setFailedPercent("0.00%");
                siteBase.setWaitingPercent("0.00%");
            }
            list.add(siteBase);
        }
        monitorInstituionDto.setData(list);
        monitorInstituionDto.setTotal(list.size());
        return monitorInstituionDto;
    }

    public MonitorSiteDto getSiteMonitor(MonitorSiteQo monitorSiteQo) {
        MonitorSiteDto monitorSiteDto = new MonitorSiteDto();
        List<TwoSiteBase> twoSiteBaseList = federatedJobStatisticsMapper.getMonitorSite(monitorSiteQo.getStartDate(), monitorSiteQo.getEndDate(), monitorSiteQo.getInstitution());
        Map<String, Map<String, List<SiteBase>>> map = new HashMap<>();
        List<String> rowSiteName = new ArrayList<>();
        for (int i = 0; i < twoSiteBaseList.size(); i++) {

            TwoSiteBase item = twoSiteBaseList.get(i);
            if (!rowSiteName.contains(item.getSiteName())) {
                rowSiteName.add(item.getSiteName());
            }
            SiteBase siteBase = new SiteBase();
            siteBase.setSiteName(item.getSiteName());
            siteBase.setSuccessJobs(item.getSuccessJobs());
            siteBase.setRunningJobs(item.getRunningJobs());
            siteBase.setFailedJobs(item.getFailedJobs());
            siteBase.setWaitingJobs(item.getWaitingJobs());
            siteBase.setTotalJobs(item.getTotalJobs());
            if (item.getTotalJobs() > 0) {
                siteBase.setSuccessPercent(String.format("%.2f", Double.valueOf(100 * item.getSuccessJobs() / item.getTotalJobs())) + "%");
                siteBase.setRunningPercent(String.format("%.2f", Double.valueOf(100 * item.getRunningJobs() / item.getTotalJobs())) + "%");
                siteBase.setFailedPercent(String.format("%.2f", Double.valueOf(100 * item.getFailedJobs() / item.getTotalJobs())) + "%");
                siteBase.setWaitingPercent(String.format("%.2f", Double.valueOf(100 * item.getWaitingJobs() / item.getTotalJobs())) + "%");
            } else {
                siteBase.setSuccessPercent("0.00%");
                siteBase.setRunningPercent("0.00%");
                siteBase.setFailedPercent("0.00%");
                siteBase.setWaitingPercent("0.00%");
            }

            if (map.containsKey(item.getInstitution())) {
                Map<String, List<SiteBase>> institutionmap = map.get(item.getInstitution());
                if (institutionmap.containsKey(item.getInstitutionSiteName())) {
                    institutionmap.get(item.getInstitutionSiteName()).add(siteBase);
                } else {
                    List<SiteBase> list = new ArrayList<>();
                    list.add(siteBase);
                    institutionmap.put(item.getInstitutionSiteName(), list);
                }
            } else {
                List<SiteBase> list = new ArrayList<>();
                list.add(siteBase);
                Map<String, List<SiteBase>> institutionmap = new HashMap<>();
                institutionmap.put(item.getInstitutionSiteName(), list);
                map.put(item.getInstitution(), institutionmap);
            }
        }
        List<MonitorSiteItem> data = new ArrayList<>();
        Integer total = 0;
        for (Map.Entry<String, Map<String, List<SiteBase>>> entry : map.entrySet()) {
            MonitorSiteItem monitorSiteItem = new MonitorSiteItem();
            String institution = entry.getKey();
            monitorSiteItem.setInstitution(institution);

            List<MonitorTwoSite> monitorTwoSiteList = new ArrayList<>();
            Map<String, List<SiteBase>> institutionmap = entry.getValue();
            for (Map.Entry<String, List<SiteBase>> entry2 : institutionmap.entrySet()) {
                total = total + 1;
                String institutionSiteName = entry2.getKey();
                List<SiteBase> siteList = entry2.getValue();
                MonitorTwoSite monitorTwoSite = new MonitorTwoSite();
                monitorTwoSite.setInstitutionSiteName(institutionSiteName);
                List<SiteBase> mixSiteModeling = new ArrayList<>();
                for (int i = 0; i < siteList.size(); i++) {
                    mixSiteModeling.add(siteList.get(i));
                }
                monitorTwoSite.setMixSiteModeling(mixSiteModeling);
                monitorTwoSiteList.add(monitorTwoSite);
            }
            monitorSiteItem.setInstitutionSiteList(monitorTwoSiteList);
            data.add(monitorSiteItem);
        }
        monitorSiteDto.setData(data);
        monitorSiteDto.setTotal(total);
        monitorSiteDto.setSiteList(rowSiteName);
        return monitorSiteDto;
    }
}
