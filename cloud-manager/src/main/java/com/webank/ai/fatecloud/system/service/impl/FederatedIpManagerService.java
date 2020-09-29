package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.CaseIdUtil;
import com.webank.ai.fatecloud.common.Enum.IpManagerEnum;
import com.webank.ai.fatecloud.common.Enum.SiteStatusEnum;
import com.webank.ai.fatecloud.common.IdNamePair;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.controller.FederatedSiteController;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedIpManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedGroupSetMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedIpManagerMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerAcceptDto;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerListDto;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerQueryDto;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerUpdateDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FederatedIpManagerService {

    @Autowired
    FederatedIpManagerMapper federatedIpManagerMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedGroupSetMapper federatedGroupSetMapper;

    public PageBean<IpManagerListDto> getIpList(IpManagerListQo ipManagerListQo) {

        String condition = ipManagerListQo.getCondition();
        if (condition != null) {
            condition = "%" + condition + "%";
        }
        ipManagerListQo.setCondition(condition);
        SiteListQo siteListQo = new SiteListQo();
        siteListQo.setCondition(ipManagerListQo.getCondition());
        siteListQo.setRole(ipManagerListQo.getRole());
        siteListQo.setOrderRule(ipManagerListQo.getUpdateTimeOrder());
        siteListQo.setStatus(2);
        siteListQo.setPageNum(ipManagerListQo.getPageNum());
        siteListQo.setPageSize(ipManagerListQo.getPageSize());
        long sitesCount = federatedSiteManagerMapper.findSitesCount(siteListQo);

        PageBean<IpManagerListDto> ipManagerListDtoPageBean = new PageBean<>(siteListQo.getPageNum(), siteListQo.getPageSize(), sitesCount);
        long startIndex = ipManagerListDtoPageBean.getStartIndex();
        List<FederatedSiteManagerDo> pagedSites = federatedSiteManagerMapper.findPagedSites(siteListQo, startIndex);
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
            ipManagerListDtos.add(ipManagerListDto);
        }
        ipManagerListDtoPageBean.setList(ipManagerListDtos);
        return ipManagerListDtoPageBean;
    }

    public Boolean dealIpModify(IpManagerUpdateQo ipManagerUpdateQo) {
        IpManagerUpdateDto ipManagerUpdateDto = new IpManagerUpdateDto();
        QueryWrapper<FederatedIpManagerDo> ewIp = new QueryWrapper<>();
        ewIp.eq(ipManagerUpdateQo.getPartyId() != null, "party_id", ipManagerUpdateQo.getPartyId());
        ewIp.eq("status", IpManagerEnum.NO_DEAL.getValue());
        ewIp.eq(ipManagerUpdateQo.getCaseId() != null, "case_id", ipManagerUpdateQo.getCaseId());
        FederatedIpManagerDo federatedIpManagerDo = federatedIpManagerMapper.selectOne(ewIp);
        if (federatedIpManagerDo != null) {
            if (ipManagerUpdateQo.getStatus().equals(1)) {
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
        if (federatedSiteManagerDo != null) {
            FederatedIpManagerDo federatedIpManagerDo = new FederatedIpManagerDo();
            federatedIpManagerDo.setPartyId(ipManagerAcceptQo.getPartyId());
            federatedIpManagerDo.setSiteName(federatedSiteManagerDo.getSiteName());
            federatedIpManagerDo.setInstitutions(federatedSiteManagerDo.getInstitutions());
            federatedIpManagerDo.setNetworkAccessEntrances(ipManagerAcceptQo.getNetworkAccessEntrances());
            federatedIpManagerDo.setNetworkAccessExits(ipManagerAcceptQo.getNetworkAccessExits());
            federatedIpManagerDo.setNetworkAccessEntrancesOld(federatedSiteManagerDo.getNetworkAccessEntrances());
            federatedIpManagerDo.setNetworkAccessExitsOld(federatedSiteManagerDo.getNetworkAccessExits());
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
        ew.eq(partyId != null, "party_id", partyId).ne("status", 0).orderByDesc("update_time");
        return federatedIpManagerMapper.selectList(ew);
    }
}
