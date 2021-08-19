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
import com.webank.ai.fatecloud.common.CaseIdUtil;
import com.webank.ai.fatecloud.common.Enum.IpManagerEnum;
import com.webank.ai.fatecloud.common.Enum.SiteStatusEnum;
import com.webank.ai.fatecloud.common.util.ObjectUtil;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.*;
import com.webank.ai.fatecloud.system.dao.mapper.*;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.IpManagerAcceptQo;
import com.webank.ai.fatecloud.system.pojo.qo.IpManagerListQo;
import com.webank.ai.fatecloud.system.pojo.qo.IpManagerQueryQo;
import com.webank.ai.fatecloud.system.pojo.qo.IpManagerUpdateQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class FederatedIpManagerService {

    @Autowired
    FederatedIpManagerMapper federatedIpManagerMapper;

    @Autowired
    FederatedFateManagerUserMapper federatedFateManagerUserMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedGroupSetMapper federatedGroupSetMapper;

    @Autowired
    PartyMapper partyMapper;

    @Autowired
    FederatedExchangeService federatedExchangeService;

    public PageBean<IpManagerListDto> getIpList(IpManagerListQo ipManagerListQo) {

        String condition = ipManagerListQo.getCondition();
        if (condition != null) {
            condition = "%" + condition + "%";
        }
        ipManagerListQo.setCondition(condition);
//        SiteListQo siteListQo = new SiteListQo();
//        siteListQo.setCondition(ipManagerListQo.getCondition());
//        siteListQo.setRole(ipManagerListQo.getRole());
//        siteListQo.setOrderRule(ipManagerListQo.getUpdateTimeOrder());
//        siteListQo.setStatus(2);
//        siteListQo.setPageNum(ipManagerListQo.getPageNum());
//        siteListQo.setPageSize(ipManagerListQo.getPageSize());
//        long sitesCount = federatedSiteManagerMapper.findSitesCount(siteListQo);
        long sitesCount = federatedSiteManagerMapper.findSitesCountForIp(ipManagerListQo);

        PageBean<IpManagerListDto> ipManagerListDtoPageBean = new PageBean<>(ipManagerListQo.getPageNum(), ipManagerListQo.getPageSize(), sitesCount);
        long startIndex = ipManagerListDtoPageBean.getStartIndex();
        List<FederatedSiteManagerDo> pagedSites = federatedSiteManagerMapper.findPagedSitesForIp(ipManagerListQo, startIndex);
        ArrayList<IpManagerListDto> ipManagerListDtos = new ArrayList<>();
        for (int i = 0; i < pagedSites.size(); i++) {
            IpManagerListDto ipManagerListDto = new IpManagerListDto(pagedSites.get(i));

            QueryWrapper<FederatedIpManagerDo> ew = new QueryWrapper<>();
            ew.eq("party_id", pagedSites.get(i).getPartyId());
            ew.orderByDesc("update_time");
            ew.last("limit 1");
            FederatedIpManagerDo federatedIpManagerDo = federatedIpManagerMapper.selectOne(ew);
            if (federatedIpManagerDo != null) {
                ipManagerListDto.setNetworkAccessExits(federatedIpManagerDo.getNetworkAccessExits());
                ipManagerListDto.setNetworkAccessEntrances(federatedIpManagerDo.getNetworkAccessEntrances());
                ipManagerListDto.setPollingStatusNew(federatedIpManagerDo.getPollingStatus());
                ipManagerListDto.setSecureStatusNew(federatedIpManagerDo.getSecureStatus());
                ipManagerListDto.setCaseId(federatedIpManagerDo.getCaseId());
                ipManagerListDto.setStatus(federatedIpManagerDo.getStatus());
                if (!federatedIpManagerDo.getStatus().equals(0)) {
                    ipManagerListDto.setUpdateTime(federatedIpManagerDo.getUpdateTime().getTime());
                } else {
                    ew.ne("status", 0);
                    FederatedIpManagerDo temp = federatedIpManagerMapper.selectOne(ew);
                    if (temp != null) {
                        ipManagerListDto.setUpdateTime(temp.getUpdateTime().getTime());
                    }
                }
            }

            QueryWrapper<FederatedGroupSetDo> ew2 = new QueryWrapper<>();
            ew2.eq("group_id", pagedSites.get(i).getGroupId());
            FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectOne(ew2);
            if (federatedGroupSetDo != null) {
                ipManagerListDto.setRole(federatedGroupSetDo.getRole());
            }

            List<FederatedIpManagerDo> federatedIpManagerDos = queryIpModifyHistory(ipManagerListDto.getPartyId());
            if (federatedIpManagerDos.size() > 0) {
                ipManagerListDto.setHistory(1);
            } else {
                ipManagerListDto.setHistory(0);
            }

            // 1.4 party exchange info
            PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(pagedSites.get(i).getPartyId());
            if (partyDetailsDto != null) {
                ipManagerListDto.setExchangeId(partyDetailsDto.getExchangeId());
                ipManagerListDto.setExchangeName(partyDetailsDto.getExchangeName());
                ipManagerListDto.setPollingStatus(partyDetailsDto.getPollingStatus());
                ipManagerListDto.setSecureStatus(partyDetailsDto.getSecureStatus());
            }

            ipManagerListDtos.add(ipManagerListDto);
        }
        ipManagerListDtoPageBean.setList(ipManagerListDtos);
        return ipManagerListDtoPageBean;
    }

    @Transactional
    public Boolean dealIpModify(IpManagerUpdateQo ipManagerUpdateQo) {
        QueryWrapper<FederatedIpManagerDo> ewIp = new QueryWrapper<>();
        ewIp.eq(ipManagerUpdateQo.getPartyId() != null, "party_id", ipManagerUpdateQo.getPartyId());
        ewIp.eq("status", IpManagerEnum.NO_DEAL.getValue());
        ewIp.eq(ipManagerUpdateQo.getCaseId() != null, "case_id", ipManagerUpdateQo.getCaseId());
        FederatedIpManagerDo federatedIpManagerDo = federatedIpManagerMapper.selectOne(ewIp);
        if (federatedIpManagerDo != null) {
            if (ipManagerUpdateQo.getStatus().equals(1)) {

                // NetworkAccessEntrances change needs to modify the route
                if (!ObjectUtil.equals(federatedIpManagerDo.getNetworkAccessEntrances(), federatedIpManagerDo.getNetworkAccessEntrancesOld())
                        || !ObjectUtil.equals(federatedIpManagerDo.getPollingStatus(), federatedIpManagerDo.getPollingStatusOld())
                        || !ObjectUtil.equals(federatedIpManagerDo.getSecureStatus(), federatedIpManagerDo.getPollingStatusOld())
                ) {
                    PartyDo partyDo = new PartyDo();
                    partyDo.setPartyId(federatedIpManagerDo.getPartyId() + "");
                    partyDo.setNetworkAccess(federatedIpManagerDo.getNetworkAccessEntrances());
                    partyDo.setPollingStatus(federatedIpManagerDo.getPollingStatus());
                    partyDo.setSecureStatus(federatedIpManagerDo.getSecureStatus());
                    federatedExchangeService.updatePartyInfo(partyDo);
                }

                QueryWrapper<FederatedSiteManagerDo> ewSite = new QueryWrapper<>();
                ewSite.eq("party_id", federatedIpManagerDo.getPartyId());
                ewSite.eq("status", SiteStatusEnum.JOINED.getCode());
                FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
                federatedSiteManagerDo.setNetworkAccessEntrances(federatedIpManagerDo.getNetworkAccessEntrances());
                federatedSiteManagerDo.setNetworkAccessExits(federatedIpManagerDo.getNetworkAccessExits());
                federatedSiteManagerDo.setUpdateTime(new Date());
                federatedSiteManagerMapper.update(federatedSiteManagerDo, ewSite);
            }
            federatedIpManagerDo.setUpdateTime(new Date());
            federatedIpManagerDo.setStatus(ipManagerUpdateQo.getStatus());
            federatedIpManagerMapper.update(federatedIpManagerDo, ewIp);
            return true;
        } else {
            log.debug("ip modify record not exists!");
        }
        return false;
    }

    public IpManagerAcceptDto acceptIpModify(IpManagerAcceptQo ipManagerAcceptQo, String appKey) {
        IpManagerAcceptDto ipManagerAcceptDto = new IpManagerAcceptDto();

        FederatedSiteManagerDo federatedSiteManagerDo = federatedSiteManagerMapper.findSiteByPartyId(ipManagerAcceptQo.getPartyId(), appKey, 2);
        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(ipManagerAcceptQo.getPartyId());
        if (federatedSiteManagerDo != null) {
            FederatedIpManagerDo federatedIpManagerDo = new FederatedIpManagerDo();
            federatedIpManagerDo.setPartyId(ipManagerAcceptQo.getPartyId());
            federatedIpManagerDo.setSiteName(federatedSiteManagerDo.getSiteName());
            federatedIpManagerDo.setInstitutions(federatedSiteManagerDo.getInstitutions());
            federatedIpManagerDo.setNetworkAccessEntrances(ipManagerAcceptQo.getNetworkAccessEntrances());
            federatedIpManagerDo.setNetworkAccessExits(ipManagerAcceptQo.getNetworkAccessExits());
            federatedIpManagerDo.setNetworkAccessEntrancesOld(federatedSiteManagerDo.getNetworkAccessEntrances());
            federatedIpManagerDo.setNetworkAccessExitsOld(federatedSiteManagerDo.getNetworkAccessExits());

            federatedIpManagerDo.setPollingStatus(ipManagerAcceptQo.getPollingStatus());
            federatedIpManagerDo.setPollingStatusOld(partyDetailsDto.getPollingStatus());
            federatedIpManagerDo.setSecureStatus(ipManagerAcceptQo.getSecureStatus());
            federatedIpManagerDo.setSecureStatusOld(partyDetailsDto.getSecureStatus());

            federatedIpManagerDo.setStatus(IpManagerEnum.NO_DEAL.getValue());
            federatedIpManagerDo.setRole(federatedSiteManagerDo.getFederatedGroupSetDo().getRole());
            federatedIpManagerDo.setGroupId(federatedSiteManagerDo.getGroupId());
            federatedIpManagerDo.setCaseId(CaseIdUtil.generateCaseId());
            if (federatedIpManagerMapper.insert(federatedIpManagerDo) > 0) {
                ipManagerAcceptDto.setCaseId(federatedIpManagerDo.getCaseId());
                return ipManagerAcceptDto;
            }
        } else {
            log.debug("party_id:{} not joined!", ipManagerAcceptQo.getPartyId());
        }
        return null;
    }

    public IpManagerQueryDto queryIpModify(IpManagerQueryQo ipManagerQueryQo) {
        IpManagerQueryDto ipManagerQueryDto = new IpManagerQueryDto();
        QueryWrapper<FederatedIpManagerDo> ew = new QueryWrapper<>();
        ew.eq(ipManagerQueryQo.getPartyId() != null, "party_id", ipManagerQueryQo.getPartyId());
        ew.eq(ipManagerQueryQo.getCaseId() != null, "case_id", ipManagerQueryQo.getCaseId());
        FederatedIpManagerDo federatedIpManagerDo = federatedIpManagerMapper.selectOne(ew);
        if (federatedIpManagerDo != null) {
            ipManagerQueryDto.setStatus(federatedIpManagerDo.getStatus());
        }
        return ipManagerQueryDto;
    }

    public List<FederatedIpManagerDo> queryIpModifyHistory(Long partyId) {
        QueryWrapper<FederatedIpManagerDo> ew = new QueryWrapper<>();
        ew.eq(partyId != null, "party_id", partyId).in("status", 1, 2).orderByDesc("update_time");
        return federatedIpManagerMapper.selectList(ew);
    }

    public List<FederatedIpManagerDo> queryUpdateIpModify(String fateManagerUserId) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("fate_manager_id", fateManagerUserId);
        FederatedFateManagerUserDo federatedFateManagerUserDo = federatedFateManagerUserMapper.selectOne(federatedFateManagerUserDoQueryWrapper);
        QueryWrapper<FederatedIpManagerDo> queryWrapper = new QueryWrapper<FederatedIpManagerDo>()
                .eq("institutions", federatedFateManagerUserDo.getInstitutions())
                .eq("status", 3);
        List<FederatedIpManagerDo> federatedIpManagerDos = federatedIpManagerMapper.selectList(queryWrapper);

        // filter
        federatedIpManagerDos.sort(Comparator.comparing(FederatedIpManagerDo::getCreateTime));
        Map<Long, FederatedIpManagerDo> longFederatedIpManagerDoMap = ObjectUtil.toMap(FederatedIpManagerDo::getPartyId, federatedIpManagerDos);

        log.info("institutions {} get new update record.", federatedFateManagerUserDo.getInstitutions());
        FederatedIpManagerDo federatedIpManagerDo = new FederatedIpManagerDo();
        federatedIpManagerDo.setUpdateTime(new Date());
        federatedIpManagerDo.setStatus(4);
        federatedIpManagerMapper.update(federatedIpManagerDo, queryWrapper);
        return new ArrayList<>(longFederatedIpManagerDoMap.values());
    }
}
