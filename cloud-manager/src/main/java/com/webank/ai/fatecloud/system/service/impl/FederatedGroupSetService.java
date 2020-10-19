package com.webank.ai.fatecloud.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.Interval;
import com.webank.ai.fatecloud.common.RangeInfo;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupDetailDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedGroupDetailMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedGroupSetMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetDto;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetRangeInfoDto;
import com.webank.ai.fatecloud.system.pojo.dto.GroupRangeCheckDto;
import com.webank.ai.fatecloud.system.pojo.dto.RangeCheckDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class FederatedGroupSetService {

    @Autowired
    FederatedGroupSetMapper federatedGroupSetMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedGroupDetailMapper federatedGroupDetailMapper;

    public FederatedGroupSetDo selecFederatedGroupSet(SiteAddQo siteAddQo) {
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<>();
        ew.eq("group_id", siteAddQo.getGroupId()).eq("status", 1);

        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectOne(ew);
        return federatedGroupSetDo;
    }

    public PageBean<FederatedGroupSetDto> selectFederatedGroupSetList(FederatedGroupSetQo federatedGroupSetQo) {
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<>();
        ew.like(federatedGroupSetQo.getGroupName() != null, "group_name", federatedGroupSetQo.getGroupName());
        ew.eq(0 != federatedGroupSetQo.getRole(), "role", federatedGroupSetQo.getRole());
        ew.eq("status", 1);
        if (federatedGroupSetQo.getOrderRule() == 1) {
            ew.orderByAsc(federatedGroupSetQo.getOrderField());
        } else {
            ew.orderByDesc(federatedGroupSetQo.getOrderField());
        }

        Integer total = federatedGroupSetMapper.selectCount(ew);
        PageBean<FederatedGroupSetDto> federatedGroupSetDtoPageBean = new PageBean<>(federatedGroupSetQo.getPageNum(), federatedGroupSetQo.getPageSize(), total);

        String limitRule = " limit " + federatedGroupSetDtoPageBean.getStartIndex() + "," + federatedGroupSetQo.getPageSize();
        ew.last(limitRule);

        List<FederatedGroupSetDo> federatedGroupSetDos = federatedGroupSetMapper.selectList(ew);


        ArrayList<FederatedGroupSetDto> federatedGroupSetDtos = new ArrayList<>();
        for (int i = 0; i < federatedGroupSetDos.size(); i++) {
            FederatedGroupSetDto federatedGroupSetDto = new FederatedGroupSetDto(federatedGroupSetDos.get(i));
            federatedGroupSetDtos.add(federatedGroupSetDto);
        }
        federatedGroupSetDtoPageBean.setList(federatedGroupSetDtos);

        return federatedGroupSetDtoPageBean;
    }

    private Long countRangeInfo(RangeInfo rangeInfo) {
        Long totalCount = 0L;
        List<Interval> intervals = rangeInfo.getIntervals();
        if (intervals != null) {
            for (Interval interval : intervals) {
                Long start = interval.getStart();
                Long end = interval.getEnd();
                totalCount = totalCount - start + end + 1;
            }
        }
        List<Long> sets = rangeInfo.getSets();
        if (sets != null) {
            totalCount = totalCount + sets.size();
        }
        return totalCount;
    }


    public void addFederatedGroupSet(FederatedGroupSetAddQo federatedGroupSetAddQo) {

        Long totalCount = countRangeInfo(federatedGroupSetAddQo.getRangeInfo());

        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo(federatedGroupSetAddQo);
        federatedGroupSetDo.setStatus(1);
        federatedGroupSetDo.setTotal(totalCount);
        federatedGroupSetMapper.insert(federatedGroupSetDo);
    }

    public int deleteFederatedGroupSet(Long groupId) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("group_id", groupId).eq("status", 2);
        Integer integer = federatedSiteManagerMapper.selectCount(ew);
        if (integer != 0) {
            return 1;
        }

        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo();
        federatedGroupSetDo.setGroupId(groupId);
        federatedGroupSetDo.setStatus(2);
        federatedGroupSetMapper.updateById(federatedGroupSetDo);
        return 0;
    }

    public void updateFederatedGroupSet(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo(federatedGroupSetUpdateQo);
        Long totalCount = countRangeInfo(federatedGroupSetUpdateQo.getRangeInfo());
        federatedGroupSetDo.setTotal(totalCount);
        federatedGroupSetMapper.updateById(federatedGroupSetDo);
    }

    public CommonResponse checkUsedSite(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        //judge input parameters
        RangeInfo rangeInfo = federatedGroupSetUpdateQo.getRangeInfo();
        List<Interval> intervals = rangeInfo.getIntervals();
        List<Long> sets = rangeInfo.getSets();
        if (intervals == null && sets == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //find used partIds in group set
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.select("party_id").eq("group_id", federatedGroupSetUpdateQo.getGroupId()).in("status", 1, 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(ew);
        if (0 == federatedSiteManagerDos.size()) {
            return new CommonResponse(ReturnCodeEnum.SUCCESS);
        }

        ArrayList<Long> partyIds = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            partyIds.add(federatedSiteManagerDo.getPartyId());
        }

        log.info("party id list:{}", partyIds);
        //check partyIds in intervals
        if (intervals != null) {
            for (Interval interval : intervals) {
                Long start = interval.getStart();
                Long end = interval.getEnd();
                for (int i = partyIds.size() - 1; i > -1; i--) {
                    if ((partyIds.get(i) >= start) && (partyIds.get(i) <= end)) {
                        partyIds.remove(i);
                        if (0 == partyIds.size()) {
                            return new CommonResponse(ReturnCodeEnum.SUCCESS);
                        }
                    }

                }
            }
        }

        //check partyIds in sets
        if (sets != null) {
            for (int i = partyIds.size() - 1; i > -1; i--) {
                if (sets.contains(partyIds.get(i))) {
                    partyIds.remove(i);
                    if (0 == partyIds.size()) {
                        return new CommonResponse(ReturnCodeEnum.SUCCESS);
                    }
                }
            }
        }

        QueryWrapper<FederatedGroupSetDo> federatedGroupSetDoQueryWrapper = new QueryWrapper<>();
        federatedGroupSetDoQueryWrapper.select("group_id", "range_info").eq("group_id", federatedGroupSetUpdateQo.getGroupId());
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectOne(federatedGroupSetDoQueryWrapper);
        String rangeInfoFromDataSource = federatedGroupSetDo.getRangeInfo();
        RangeInfo rangeInfoObject = JSON.parseObject(rangeInfoFromDataSource, new TypeReference<RangeInfo>() {
        });
        List<Interval> intervalsFromDataSource = rangeInfoObject.getIntervals();

        GroupRangeCheckDto groupRangeCheckDto = new GroupRangeCheckDto();

        for (int i = partyIds.size() - 1; i > -1; i--) {
            for (Interval interval : intervalsFromDataSource) {
                Long start = interval.getStart();
                Long end = interval.getEnd();
                if ((partyIds.get(i) >= start) && (partyIds.get(i) <= end)) {
                    if (groupRangeCheckDto.getIntervalWithPartyIds().get(interval) == null) {
                        ArrayList<Long> usedPartyIdsInInterval = new ArrayList<>();
                        usedPartyIdsInInterval.add(partyIds.get(i));
                        groupRangeCheckDto.getIntervalWithPartyIds().put(interval, usedPartyIdsInInterval);
                        partyIds.remove(i);
                        if (0 == partyIds.size()) {
                            RangeCheckDto rangeCheckDto = new RangeCheckDto(groupRangeCheckDto);
                            return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR, rangeCheckDto);
                        }
                    } else {
                        List<Long> longs = groupRangeCheckDto.getIntervalWithPartyIds().get(interval);
                        longs.add(partyIds.get(i));
                        partyIds.remove(i);
                        if (0 == partyIds.size()) {
                            RangeCheckDto rangeCheckDto = new RangeCheckDto(groupRangeCheckDto);
                            return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR, rangeCheckDto);
                        }
                    }
                }
            }
        }
        groupRangeCheckDto.setSets(partyIds);

        RangeCheckDto rangeCheckDto = new RangeCheckDto(groupRangeCheckDto);
        return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR, rangeCheckDto);

    }

    public int checkFederatedGroupSet(FederatedGroupSetCheckQo federatedGroupSetCheckQo) {

        RangeInfo rangeInfo = federatedGroupSetCheckQo.getRangeInfo();
        List<Interval> intervals = rangeInfo.getIntervals();
        List<Long> sets = rangeInfo.getSets();

        if (intervals == null && sets == null) {
            return 1;
        }
// judge sets from input
        HashSet<Object> setsObjects = new HashSet<>();
        if (sets != null) {
            for (int l = 0; l < sets.size(); l++) {
                setsObjects.add(sets.get(l));
            }

            if (sets.size() != setsObjects.size()) {
                return 1;
            }
        }
//judge intervals form input
        if (intervals != null) {
            for (int i = 0; i < intervals.size(); i++) {
                Interval interval = intervals.get(i);
                Long start = interval.getStart();
                Long end = interval.getEnd();
                if (start >= end) {
                    return 2;
                }

                for (int j = 0; j < intervals.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    Interval comparedInterval = intervals.get(j);
                    Long comparedStart = comparedInterval.getStart();
                    Long comparedEnd = comparedInterval.getEnd();

                    if (comparedStart >= comparedEnd) {
                        return 1;
                    }

                    if ((start >= comparedStart) && (start <= comparedEnd)) {
                        return 2;
                    }
                    if ((end >= comparedStart) && (end <= comparedEnd)) {
                        return 2;
                    }
                }

                if (sets != null) {
                    for (int k = 0; k < sets.size(); k++) {
                        Long partyId = sets.get(k);
                        if ((partyId >= start) && (partyId <= end)) {
                            return 1;
                        }
                    }
                }


            }
        }

        //compare with range info from datasource
        QueryWrapper<FederatedGroupSetDo> federatedGroupSetDoQueryWrapper = new QueryWrapper<>();
        federatedGroupSetDoQueryWrapper.select("range_info").eq("status", 1);

        if (federatedGroupSetCheckQo.getGroupId() != null) {
            federatedGroupSetDoQueryWrapper.ne("group_id", federatedGroupSetCheckQo.getGroupId());
        }

        List<Map<String, Object>> rangeInfoMaps = federatedGroupSetMapper.selectMaps(federatedGroupSetDoQueryWrapper);
        for (Map<String, Object> rangeInfoMap : rangeInfoMaps) {
            Object rangeInfoObject = rangeInfoMap.get("range_info");
            String s = String.valueOf(rangeInfoObject);
            JSONObject comparedRangeInfoJsonObject = JSON.parseObject(String.valueOf(rangeInfoObject));
            JSONArray comparedIntervals = comparedRangeInfoJsonObject.getJSONArray("intervals");
            JSONArray comparedSets = comparedRangeInfoJsonObject.getJSONArray("sets");

            //compare with intervals from datasource
            if (intervals != null) {
                for (Interval interval : intervals) {

                    Long start = interval.getStart();
                    Long end = interval.getEnd();
                    if (comparedIntervals != null) {
                        for (Object comparedInterval : comparedIntervals) {
                            JSONObject comparedIntervalJsonObject = JSON.parseObject(String.valueOf(comparedInterval));
                            Long comparedStart = comparedIntervalJsonObject.getLong("start");
                            Long comparedEnd = comparedIntervalJsonObject.getLong("end");

                            if ((start >= comparedStart) && (start <= comparedEnd)) {
                                return 2;
                            }
                            if ((end >= comparedStart) && (end <= comparedEnd)) {
                                return 2;
                            }
                        }
                    }
                    if (comparedSets != null) {
                        for (Object comparedSet : comparedSets) {
                            Long partyId = Long.valueOf(comparedSet.toString());
                            if ((partyId >= start) && (partyId <= end)) {
                                return 2;
                            }
                        }
                    }
                }
            }

            //compare with sets from datasource
            if (sets != null) {
                for (Object set : sets) {
                    Long partyId = Long.valueOf(set.toString());
                    if (comparedIntervals != null) {
                        for (Object comparedInterval : comparedIntervals) {
                            String s1 = String.valueOf(comparedInterval);
                            JSONObject comparedIntervalJsonObject = JSON.parseObject(String.valueOf(comparedInterval));
                            Long comparedStart = comparedIntervalJsonObject.getLong("start");
                            Long comparedEnd = comparedIntervalJsonObject.getLong("end");

                            if ((partyId >= comparedStart) && (partyId <= comparedEnd)) {
                                return 2;
                            }
                        }
                    }

                    if (comparedSets != null) {
                        HashSet<Long> partyIds = new HashSet<>();
                        for (Object comparedSet : comparedSets) {
                            partyIds.add(Long.valueOf(String.valueOf(comparedSet)));
                        }
                        if (partyIds.contains(partyId)) {
                            return 2;
                        }
                    }

                }
            }

        }
        return 0;
    }


    public PageBean<FederatedGroupSetRangeInfoDto> findPagedRangeInfos(PageInfoQo pageInfoQo) {
        QueryWrapper<FederatedGroupSetDo> ewForCount = new QueryWrapper<>();
        ewForCount.eq("status", 1).eq(pageInfoQo.getRole() != null, "role", pageInfoQo.getRole());
        Integer count = federatedGroupSetMapper.selectCount(ewForCount);
        PageBean<FederatedGroupSetRangeInfoDto> rangeInfoPageBean = new PageBean<>(pageInfoQo.getPageNum(), pageInfoQo.getPageSize(), count);

        String limitRule = " limit " + rangeInfoPageBean.getStartIndex() + "," + pageInfoQo.getPageSize();
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<FederatedGroupSetDo>();
        ew.select("range_info", "group_id", "group_name").eq("status", 1).eq(pageInfoQo.getRole() != null, "role", pageInfoQo.getRole());
        ew.last(limitRule);

        List<FederatedGroupSetDo> federatedGroupSetDos = federatedGroupSetMapper.selectList(ew);

        ArrayList<FederatedGroupSetRangeInfoDto> federatedGroupSetRangeInfoDtos = new ArrayList<>();
        for (int i = 0; i < federatedGroupSetDos.size(); i++) {
            FederatedGroupSetRangeInfoDto federatedGroupSetRangeInfoDto = new FederatedGroupSetRangeInfoDto(federatedGroupSetDos.get(i));
            federatedGroupSetRangeInfoDtos.add(federatedGroupSetRangeInfoDto);
        }
        rangeInfoPageBean.setList(federatedGroupSetRangeInfoDtos);

        return rangeInfoPageBean;
    }

    public boolean checkGroupName(String groupName) {
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<>();
        ew.select("group_id").eq("group_name", groupName).eq("status", 1);
        List<FederatedGroupSetDo> federatedGroupSetDos = federatedGroupSetMapper.selectList(ew);
        return federatedGroupSetDos.size() > 0;
    }

    public boolean checkGroupName(FederatedGroupSetNameCheckQo federatedGroupSetNameCheckQo) {
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<>();
        ew.select("group_id").eq("group_name", federatedGroupSetNameCheckQo.getGroupName()).eq("status", 1).ne(federatedGroupSetNameCheckQo.getGroupId() != null, "group_id", federatedGroupSetNameCheckQo.getGroupId());
        List<FederatedGroupSetDo> federatedGroupSetDos = federatedGroupSetMapper.selectList(ew);
        return federatedGroupSetDos.size() > 0;
    }

    public CommonResponse checkGroupRegion(Long groupId, List<Region> regions) {
//        check input regions
//        1.check each region range
        for (Region region : regions) {
            if (0 >= region.getLeftRegion() || 0 >= region.getRightRegion() || region.getLeftRegion() > region.getRightRegion()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
        }
//        2.check duplicated range between regions
        for (int i = regions.size() - 1; i > 0; i--) {
            Region region = regions.get(i);
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();

            for (int j = i - 1; j > -1; j--) {
                Region comparedRegion = regions.get(j);
                Long comparedLeftRegion = comparedRegion.getLeftRegion();
                Long comparedRightRegion = comparedRegion.getRightRegion();
                if ((leftRegion >= comparedLeftRegion) && (leftRegion <= comparedRightRegion)) {
                    return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
                }
                if ((rightRegion >= comparedLeftRegion) && (rightRegion <= comparedRightRegion)) {
                    return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
                }
                if ((comparedLeftRegion >= leftRegion) && (comparedLeftRegion <= rightRegion)) {
                    return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
                }
            }
        }

//        check regions in databases
//        1.query regions in databases
        QueryWrapper<FederatedGroupDetailDo> federatedGroupDetailDoQueryWrapper = new QueryWrapper<>();
        federatedGroupDetailDoQueryWrapper.select("id", "left_region", "right_region").eq("status", 1);
        federatedGroupDetailDoQueryWrapper.ne(groupId != null, "group_id", groupId);
        List<FederatedGroupDetailDo> federatedGroupDetailDos = federatedGroupDetailMapper.selectList(federatedGroupDetailDoQueryWrapper);
//        2.check regions in databases
        if (0 < federatedGroupDetailDos.size()) {
            for (Region region : regions) {
                Long leftRegion = region.getLeftRegion();
                Long rightRegion = region.getRightRegion();
                for (FederatedGroupDetailDo federatedGroupDetailDo : federatedGroupDetailDos) {
                    Long comparedLeftRegion = federatedGroupDetailDo.getLeftRegion();
                    Long comparedRightRegion = federatedGroupDetailDo.getRightRegion();
                    if ((leftRegion >= comparedLeftRegion) && (leftRegion <= comparedRightRegion)) {
                        return new CommonResponse<>(ReturnCodeEnum.GROUP_REGIONS_ERROR, new Region(comparedLeftRegion, comparedRightRegion));
                    }
                    if ((rightRegion >= comparedLeftRegion) && (rightRegion <= comparedRightRegion)) {
                        return new CommonResponse<>(ReturnCodeEnum.GROUP_REGIONS_ERROR, new Region(comparedLeftRegion, comparedRightRegion));
                    }
                    if ((comparedLeftRegion >= leftRegion) && (comparedLeftRegion <= rightRegion)) {
                        return new CommonResponse<>(ReturnCodeEnum.GROUP_REGIONS_ERROR, new Region(comparedLeftRegion, comparedRightRegion));
                    }
                }
            }
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    @Transactional
    public void addNewGroup(GroupAddQo groupAddQo) {
        //insert into table t_federated_group_set
        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo(groupAddQo);
        Long sum = 0L;
        List<Region> regions = groupAddQo.getRegions();
        for (Region region : regions) {
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();
            sum = sum + rightRegion - leftRegion + 1;
        }
        federatedGroupSetDo.setTotal(sum);
        federatedGroupSetMapper.insert(federatedGroupSetDo);

        //insert into table t_federated_group_range_detail
        for (Region region : regions) {
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();
            FederatedGroupDetailDo federatedGroupDetailDo = new FederatedGroupDetailDo();
            federatedGroupDetailDo.setLeftRegion(leftRegion);
            federatedGroupDetailDo.setRightRegion(rightRegion);
            federatedGroupDetailDo.setGroupId(federatedGroupSetDo.getGroupId());
            federatedGroupDetailDo.setSize(rightRegion - leftRegion + 1);
            federatedGroupDetailMapper.insert(federatedGroupDetailDo);
        }

    }

    @Transactional
    public void deleteNewGroup(Long groupId) {
//      update table t_federated_group_set
        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo();
        federatedGroupSetDo.setGroupId(groupId);
        federatedGroupSetDo.setStatus(2);
        federatedGroupSetMapper.updateById(federatedGroupSetDo);

//      delete table t_federated_group_range_detail
        QueryWrapper<FederatedGroupDetailDo> federatedGroupDetailDoQueryWrapper = new QueryWrapper<>();
        federatedGroupDetailDoQueryWrapper.eq("group_id", groupId);
        federatedGroupDetailMapper.delete(federatedGroupDetailDoQueryWrapper);
    }

    public CommonResponse checkUsedSite(GroupUpdateQo groupUpdateQo) {
        List<Region> regions = groupUpdateQo.getRegions();
        if (regions == null || 0 == regions.size()) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

//        find used party ids in this group
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.select("party_id").eq("group_id", groupUpdateQo.getGroupId()).in("status", 1, 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        if (0 == federatedSiteManagerDos.size()) {
            return new CommonResponse(ReturnCodeEnum.SUCCESS);
        }
        ArrayList<Long> partyIds = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            partyIds.add(federatedSiteManagerDo.getPartyId());
        }

//        get part ids not in the new regions
        for (Region region : regions) {
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();
            for (int i = partyIds.size() - 1; i > -1; i--) {
                if ((partyIds.get(i) >= leftRegion) && (partyIds.get(i) <= rightRegion)) {
                    partyIds.remove(i);
                    if (0 == partyIds.size()) {
                        return new CommonResponse(ReturnCodeEnum.SUCCESS);
                    }
                }
            }
        }


//        get the party ids and its belonged region, which not in the new regions.
        QueryWrapper<FederatedGroupDetailDo> federatedGroupDetailDoQueryWrapper = new QueryWrapper<>();
        federatedGroupDetailDoQueryWrapper.select("id", "left_region", "right_region").eq("group_id", groupUpdateQo.getGroupId());
        List<FederatedGroupDetailDo> federatedGroupDetailDos = federatedGroupDetailMapper.selectList(federatedGroupDetailDoQueryWrapper);
        LinkedList<RegionWithUsedPartyId> regionWithUsedPartyIds = new LinkedList<>();
        for (FederatedGroupDetailDo federatedGroupDetailDo : federatedGroupDetailDos) {
            RegionWithUsedPartyId regionWithUsedPartyId = new RegionWithUsedPartyId();
//            Region region = new Region(federatedGroupDetailDo.getLeftRegion(), federatedGroupDetailDo.getRightRegion());
            regionWithUsedPartyId.getRegion().setLeftRegion(federatedGroupDetailDo.getLeftRegion());
            regionWithUsedPartyId.getRegion().setRightRegion(federatedGroupDetailDo.getRightRegion());

            for (int i = partyIds.size() - 1; i > -1; i--) {
                if ((partyIds.get(i) >= federatedGroupDetailDo.getLeftRegion()) && (partyIds.get(i) <= federatedGroupDetailDo.getRightRegion())) {
                    regionWithUsedPartyId.getUsedPartyIds().add(partyIds.get(i));
                    partyIds.remove(i);
                }
            }
            if (0 != regionWithUsedPartyId.getUsedPartyIds().size()) {
                regionWithUsedPartyIds.add(regionWithUsedPartyId);
            }
            if (0 == partyIds.size()) {
                return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR, regionWithUsedPartyIds);
            }
        }

        return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR, partyIds);

    }

    public Integer findUsedPartyIdsOfGroup(Long groupId) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("group_id", groupId).in("status", 1, 2);
        return federatedSiteManagerMapper.selectCount(ew);
    }

    public void updateGroup(GroupUpdateQo groupUpdateQo) {
        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo(groupUpdateQo);
        List<Region> regions = groupUpdateQo.getRegions();
        Long sum = 0L;
        for (Region region : regions) {
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();
            sum = sum + rightRegion - leftRegion + 1;
        }
        federatedGroupSetDo.setTotal(sum);
        federatedGroupSetMapper.updateById(federatedGroupSetDo);

        //      delete table t_federated_group_range_detail
        QueryWrapper<FederatedGroupDetailDo> federatedGroupDetailDoQueryWrapper = new QueryWrapper<>();
        federatedGroupDetailDoQueryWrapper.eq("group_id", groupUpdateQo.getGroupId());
        federatedGroupDetailMapper.delete(federatedGroupDetailDoQueryWrapper);

        //insert into table t_federated_group_range_detail
        for (Region region : regions) {
            Long leftRegion = region.getLeftRegion();
            Long rightRegion = region.getRightRegion();
            FederatedGroupDetailDo federatedGroupDetailDo = new FederatedGroupDetailDo();
            federatedGroupDetailDo.setLeftRegion(leftRegion);
            federatedGroupDetailDo.setRightRegion(rightRegion);
            federatedGroupDetailDo.setGroupId(federatedGroupSetDo.getGroupId());
            federatedGroupDetailDo.setSize(rightRegion - leftRegion + 1);
            federatedGroupDetailMapper.insert(federatedGroupDetailDo);
        }

    }

    public PageBean<FederatedGroupSetDo> selectNewGroupSetList(FederatedGroupSetQo federatedGroupSetQo) {
        QueryWrapper<FederatedGroupSetDo> federatedGroupSetDoQueryWrapper = new QueryWrapper<>();
        federatedGroupSetDoQueryWrapper.like(federatedGroupSetQo.getGroupName() != null, "group_name", federatedGroupSetQo.getGroupName());
        federatedGroupSetDoQueryWrapper.eq(federatedGroupSetQo.getRole() != null && 0 != federatedGroupSetQo.getRole(), "role", federatedGroupSetQo.getRole());
        federatedGroupSetDoQueryWrapper.eq("status", 1);
        if (federatedGroupSetQo.getOrderRule() != null) {
            if (federatedGroupSetQo.getOrderRule() == 1) {
                federatedGroupSetDoQueryWrapper.orderByAsc(federatedGroupSetQo.getOrderField());
            } else {
                federatedGroupSetDoQueryWrapper.orderByDesc(federatedGroupSetQo.getOrderField());
            }
        }

        Integer total = federatedGroupSetMapper.selectCount(federatedGroupSetDoQueryWrapper);
        PageBean<FederatedGroupSetDo> federatedGroupSetDtoPageBean = new PageBean<>(federatedGroupSetQo.getPageNum(), federatedGroupSetQo.getPageSize(), total);

        if (federatedGroupSetQo.getGroupName() != null) {
            federatedGroupSetQo.setGroupName("%" + federatedGroupSetQo.getGroupName() + "%");
        }
        List<FederatedGroupSetDo> federatedGroupSetDos = federatedGroupSetMapper.selectNewList(federatedGroupSetQo, federatedGroupSetDtoPageBean.getStartIndex());
        federatedGroupSetDtoPageBean.setList(federatedGroupSetDos);
        return federatedGroupSetDtoPageBean;
    }

    public PageBean<FederatedGroupSetDo> findPagedRegionInfoNew(PageInfoQo pageInfoQo) {
        QueryWrapper<FederatedGroupSetDo> federatedGroupSetDoQueryWrapper = new QueryWrapper<>();
        federatedGroupSetDoQueryWrapper.eq("status", 1).eq(pageInfoQo.getRole() != null, "role", pageInfoQo.getRole());
        Integer sum = federatedGroupSetMapper.selectCount(federatedGroupSetDoQueryWrapper);
        PageBean<FederatedGroupSetDo> federatedGroupSetDoPageBean = new PageBean<>(pageInfoQo.getPageNum(), pageInfoQo.getPageSize(), sum);

        List<FederatedGroupSetDo> pagedRegionInfo = federatedGroupSetMapper.findPagedRegionInfo(pageInfoQo, federatedGroupSetDoPageBean.getStartIndex());
        federatedGroupSetDoPageBean.setList(pagedRegionInfo);
        return federatedGroupSetDoPageBean;
    }

    public boolean checkGroupStatus(GroupUpdateQo groupUpdateQo) {
        QueryWrapper<FederatedGroupSetDo> federatedGroupSetDoQueryWrapper = new QueryWrapper<>();
        federatedGroupSetDoQueryWrapper.select();
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectById(groupUpdateQo.getGroupId());
        Integer status = federatedGroupSetDo.getStatus();
        return 1 == status;
    }

    public FederatedGroupSetDo selectGroupSetDetails(SiteAddQo siteAddQo) {
        if (siteAddQo == null || siteAddQo.getGroupId() == null) {
            return null;
        }
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectGroupSetDetails(siteAddQo.getGroupId());
        return federatedGroupSetDo;
    }
}
