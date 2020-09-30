package com.webank.ai.fatecloud.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.*;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.KeyAndSecretGenerate;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.*;
import com.webank.ai.fatecloud.system.dao.mapper.*;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.dto.UsedSiteDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@Slf4j
@Service
public class FederatedSiteManagerService {

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedGroupSetMapper federatedGroupSetMapper;

    @Autowired
    FederatedOrganizationMapper federatedOrganizationMapper;

    @Autowired
    FederatedFateManagerUserMapper federatedFateManagerUserMapper;


    @Value(value = "${cloud-manager.ip}")
    String ip;


    @Value(value = "${server.servlet.context-path}")
    String prefix;

    public PageBean<SiteDetailDto> findPagedSites(SiteListQo siteListQo) {
        String condition = siteListQo.getCondition();
        if (condition != null) {
            condition = "%" + condition + "%";
        }
        siteListQo.setCondition(condition);
        long sitesCount = federatedSiteManagerMapper.findSitesCount(siteListQo);

        PageBean<SiteDetailDto> siteDetailDtoPageBean = new PageBean<>(siteListQo.getPageNum(), siteListQo.getPageSize(), sitesCount);
        long startIndex = siteDetailDtoPageBean.getStartIndex();
        List<FederatedSiteManagerDo> pagedSites = federatedSiteManagerMapper.findPagedSites(siteListQo, startIndex);
        ArrayList<SiteDetailDto> siteDetailDtos = new ArrayList<>();
        for (int i = 0; i < pagedSites.size(); i++) {
            SiteDetailDto siteDetailDto = new SiteDetailDto(pagedSites.get(i));
            siteDetailDtos.add(siteDetailDto);
        }
        siteDetailDtoPageBean.setList(siteDetailDtos);
        return siteDetailDtoPageBean;
    }

    public boolean checkPartyIdExist(PartyIdQo partyIdQo) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("party_id", partyIdQo.getPartyId()).in("status", 1, 2).ne(partyIdQo.getId() != null, "id", partyIdQo.getId());
        Integer integer = federatedSiteManagerMapper.selectCount(ew);
        return integer > 0;
    }

    @Transactional
    public Long addSite(SiteAddQo siteAddQo) throws UnsupportedEncodingException {
        //insert site
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo(siteAddQo);
        String appKey = KeyAndSecretGenerate.getAppKey();
        String appSecret = KeyAndSecretGenerate.getAppSecret(appKey);
        HashMap<String, String> secretMap = new HashMap<>();
        secretMap.put("key", appKey);
        secretMap.put("secret", appSecret);
        String secretInfo = JSON.toJSONString(secretMap);
        federatedSiteManagerDo.setSecretInfo(secretInfo);

        federatedSiteManagerMapper.insert(federatedSiteManagerDo);

        Long groupId = federatedSiteManagerDo.getGroupId();
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectById(groupId);
        QueryWrapper<FederatedOrganizationDo> query = new QueryWrapper<>();
        query.eq("status", 1);
        FederatedOrganizationDo federatedOrganizationDo = federatedOrganizationMapper.selectOne(query);
        Integer role = federatedGroupSetDo.getRole();
        SiteActivateUrl siteActivateUrl = new SiteActivateUrl(federatedSiteManagerDo, role, federatedOrganizationDo.getName(), federatedOrganizationDo.getId(), federatedGroupSetDo.getGroupName());

        String info = JSON.toJSONString(siteActivateUrl);
        log.info("info:{}", info);
        String url = ip + prefix + "/api/site/activate" + "?st=" + info.replace("\"{", "{").replace("}\"", "}").replace("\\", "").replace("\"", "\\\"");
        String encodedUrl = EncryptUtil.encode(url);

        FederatedSiteManagerDo federatedSiteManagerDoUpdate = new FederatedSiteManagerDo();
        federatedSiteManagerDoUpdate.setRegistrationLink(encodedUrl);
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.eq("id", federatedSiteManagerDo.getId());
        federatedSiteManagerMapper.update(federatedSiteManagerDoUpdate, federatedSiteManagerDoQueryWrapper);

        //update group set
        Long used = federatedGroupSetDo.getUsed();
        used++;
        FederatedGroupSetDo federatedGroupSetDoUpdate = new FederatedGroupSetDo();
        federatedGroupSetDoUpdate.setUsed(used);
        QueryWrapper<FederatedGroupSetDo> ew = new QueryWrapper<>();
        ew.eq("group_id", groupId);
        federatedGroupSetMapper.update(federatedGroupSetDoUpdate, ew);

        return federatedSiteManagerDo.getId();
    }

    public SiteDetailDto findSite(Long id) {
        FederatedSiteManagerDo site = federatedSiteManagerMapper.findSite(id);
        return site == null ? null : new SiteDetailDto(site);
    }

    public SiteDetailDto findSiteByPartyId(Long partyId, String secretInfo, Object... status) {
        FederatedSiteManagerDo site = federatedSiteManagerMapper.findSiteByPartyId(partyId, secretInfo, status);
        if (site != null) {
            return new SiteDetailDto(site);
        }
        return null;
    }

    public FederatedSiteManagerDo findSiteDoByPartyId(Long partyId, Object... status) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.select("id", "registration_link").eq("party_id", partyId).in("status", status);

        List<FederatedSiteManagerDo> sites = federatedSiteManagerMapper.selectList(ew);
        if (null == sites || 1 != sites.size()) {
            return null;
        }
        return sites.get(0);
    }

    @Transactional
    public void deleteSite(Long id) {
        //delete site
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("id", id);
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setStatus(3);
        federatedSiteManagerDo.setDetectiveStatus(1);
        federatedSiteManagerMapper.update(federatedSiteManagerDo, ew);

        //update group set
        FederatedSiteManagerDo federatedSiteManagerDoFromDatasource = federatedSiteManagerMapper.selectById(id);
        Long groupId = federatedSiteManagerDoFromDatasource.getGroupId();

        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectById(groupId);
        Long used = federatedGroupSetDo.getUsed();
        used--;
        FederatedGroupSetDo federatedGroupSetDoForUpdate = new FederatedGroupSetDo();
        federatedGroupSetDoForUpdate.setUsed(used);
        QueryWrapper<FederatedGroupSetDo> ewForUpdate = new QueryWrapper<>();
        ewForUpdate.eq("group_id", groupId);
        federatedGroupSetMapper.update(federatedGroupSetDoForUpdate, ewForUpdate);
    }

    @Transactional
    public void updateSite(SiteUpdateQo siteUpdateQo, Long originGroupId) throws UnsupportedEncodingException {
        //update site
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("id", siteUpdateQo.getId());
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo(siteUpdateQo);

        FederatedSiteManagerDo federatedSiteManagerDoForSecretInfo = federatedSiteManagerMapper.selectById(siteUpdateQo.getId());
        federatedSiteManagerDo.setSecretInfo(federatedSiteManagerDoForSecretInfo.getSecretInfo());
        Long groupId = siteUpdateQo.getGroupId();
        FederatedGroupSetDo federatedGroupSetDoForUpdate = federatedGroupSetMapper.selectById(groupId);
        QueryWrapper<FederatedOrganizationDo> query = new QueryWrapper<>();
        query.eq("status", 1);
        FederatedOrganizationDo federatedOrganizationDo = federatedOrganizationMapper.selectOne(query);
        Integer role = federatedGroupSetDoForUpdate.getRole();
        SiteActivateUrl siteActivateUrl = new SiteActivateUrl(federatedSiteManagerDo, role, federatedOrganizationDo.getName(), federatedOrganizationDo.getId(), federatedGroupSetDoForUpdate.getGroupName());

        String info = JSON.toJSONString(siteActivateUrl);
        log.info("info:{}", info);
        String url = ip + prefix + "/api/site/activate" + "?st=" + info.replace("\"{", "{").replace("}\"", "}").replace("\\", "").replace("\"", "\\\"");
        String encodedUrl = EncryptUtil.encode(url);
        federatedSiteManagerDo.setRegistrationLink(encodedUrl);

        federatedSiteManagerMapper.update(federatedSiteManagerDo, ew);

        //update group joined used field
        QueryWrapper<FederatedGroupSetDo> newGroupQWForAdd = new QueryWrapper<>();
        newGroupQWForAdd.eq("group_id", siteUpdateQo.getGroupId());
        FederatedGroupSetDo groupDoForAdd = federatedGroupSetMapper.selectOne(newGroupQWForAdd);

        FederatedGroupSetDo federatedGroupSetDo = new FederatedGroupSetDo();
        federatedGroupSetDo.setUsed(groupDoForAdd.getUsed() + 1);
        federatedGroupSetMapper.update(federatedGroupSetDo, newGroupQWForAdd);

        //update group removed used field
        QueryWrapper<FederatedGroupSetDo> newGroupQWForRemove = new QueryWrapper<>();
        newGroupQWForRemove.eq("group_id", originGroupId);
        FederatedGroupSetDo groupDoForRemove = federatedGroupSetMapper.selectOne(newGroupQWForRemove);

        FederatedGroupSetDo federatedGroupSetDoForRemove = new FederatedGroupSetDo();
        federatedGroupSetDoForRemove.setUsed(groupDoForRemove.getUsed() - 1);
        federatedGroupSetMapper.update(federatedGroupSetDoForRemove, newGroupQWForRemove);
    }

    public void activateSite(Long partyId) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("party_id", partyId).in("status", 1);
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        Date date = new Date();
        federatedSiteManagerDo.setStatus(2);
        federatedSiteManagerDo.setActivationTime(date);
        federatedSiteManagerDo.setDetectiveStatus(2);
        federatedSiteManagerDo.setLastDetectiveTime(date);
        federatedSiteManagerMapper.update(federatedSiteManagerDo, ew);
    }

    public int checkPartyId(SitePartyIdCheckQo sitePartyIdCheckQo) {
        Long partyId = sitePartyIdCheckQo.getPartyId();
        RangeInfo rangeInfo = sitePartyIdCheckQo.getRangeInfo();
        List<Long> sets = rangeInfo.getSets();
        List<Interval> intervals = rangeInfo.getIntervals();

        if (sets.contains(partyId)) {
            return 0;
        }
        for (Interval interval : intervals) {
            Long start = interval.getStart();
            Long end = interval.getEnd();
            if (partyId >= start && partyId <= end) {
                return 0;
            }
        }
        return 1;
    }

    public boolean checkWeb(TelnetIpQo telnetIpQo) {
        Socket connect = new Socket();
        try {
            connect.connect(new InetSocketAddress(telnetIpQo.getIp(), telnetIpQo.getPort()), 3);
            boolean res = connect.isConnected();
            log.debug("Connect success!~~~~ {} : {}", telnetIpQo.getIp(), telnetIpQo.getPort());
            return res;
        } catch (IOException e) {
            log.debug("Connect error!~~~~ {} : {}", telnetIpQo.getIp(), telnetIpQo.getPort());
            return false;
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                log.error("Socket closing error!~~~~ {} : {}", telnetIpQo.getIp(), telnetIpQo.getPort());
            }
        }
    }

//    public PageBean<UsedSiteDto> findUsedSites(UsedSiteListQo usedSiteListQo) {
//        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
//        String condition = usedSiteListQo.getCondition();
//        Integer status = usedSiteListQo.getStatus();
//        Long groupId = usedSiteListQo.getGroupId();
//        if (condition != null) {
//            ew.nested(i -> i.like("party_id", condition).or().like("site_name", condition));
//        }
//        if (status == null) {
//            ew.in("status", 1, 2, 3);
//        } else if ((1 == status) || (2 == status)) {
//
//            ew.in("status", 1, 2);
//        } else {
//            ew.in("status", status);
//        }
//
//        ew.eq(groupId != null, "group_id", groupId);
//        Integer total = federatedSiteManagerMapper.selectCount(ew);
//        PageBean<UsedSiteDto> federatedPartySetDoPage = new PageBean<>(usedSiteListQo.getPageNum(), usedSiteListQo.getPageSize(), total);
//        long startIndex = federatedPartySetDoPage.getStartIndex();
//
//        if (condition != null) {
//            usedSiteListQo.setCondition("%" + condition + "%");
//        }
//        List<FederatedSiteManagerDo> usedSites = federatedSiteManagerMapper.findUsedSites(usedSiteListQo, startIndex);
//        ArrayList<UsedSiteDto> usedSiteDtos = new ArrayList<>();
//        for (int i = 0; i < usedSites.size(); i++) {
//            UsedSiteDto usedSiteDto = new UsedSiteDto(usedSites.get(i));
//            usedSiteDtos.add(usedSiteDto);
//        }
//        federatedPartySetDoPage.setList(usedSiteDtos);
//        return federatedPartySetDoPage;
//    }

    public PageBean<UsedSiteDto> findUsedSites(UsedSiteListQo usedSiteListQo) {

        String condition = usedSiteListQo.getCondition();
        if (condition != null) {
            usedSiteListQo.setCondition("%" + condition + "%");
        }
        long usedSitesCount = federatedSiteManagerMapper.findUsedSitesCount(usedSiteListQo);

        PageBean<UsedSiteDto> federatedPartySetDoPage = new PageBean<>(usedSiteListQo.getPageNum(), usedSiteListQo.getPageSize(), usedSitesCount);
        long startIndex = federatedPartySetDoPage.getStartIndex();

        List<FederatedSiteManagerDo> usedSites = federatedSiteManagerMapper.findUsedSites(usedSiteListQo, startIndex);
        ArrayList<UsedSiteDto> usedSiteDtos = new ArrayList<>();
        for (int i = 0; i < usedSites.size(); i++) {
            UsedSiteDto usedSiteDto = new UsedSiteDto(usedSites.get(i));
            usedSiteDtos.add(usedSiteDto);
        }
        federatedPartySetDoPage.setList(usedSiteDtos);
        return federatedPartySetDoPage;
    }

    public FederatedSiteManagerDo checkSiteSurvive(Long partId) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("party_id", partId).eq("status", 2).eq("detective_status", "2");
        FederatedSiteManagerDo federatedSiteManagerDo = federatedSiteManagerMapper.selectOne(ew);
        return federatedSiteManagerDo;
    }

    public CommonResponse checkSiteAuthority(String siteInfo) {
        JSONObject jsonObject = JSON.parseObject(siteInfo);

        String httpURI = jsonObject.getString(Dict.HTTP_URI);
        String httpBody = jsonObject.getString(Dict.HTTP_BODY);
        Preconditions.checkArgument(StringUtils.isNoneEmpty(jsonObject.getString(Dict.HTTP_HEADER), httpURI, httpBody));

        JSONObject headers = jsonObject.getJSONObject(Dict.HTTP_HEADER);
        String signature = headers.getString(Dict.SIGNATURE);
        String partyId = headers.getString(Dict.PARTY_ID);
        String role = headers.getString(Dict.ROLE);
        String appKey = headers.getString(Dict.APP_KEY);
        String timestamp = headers.getString(Dict.TIMESTAMP);
        String nonce = headers.getString(Dict.NONCE);
        Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, partyId, role, appKey, timestamp, nonce));

        log.info("Head Information | partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}", partyId, role, appKey, timestamp, nonce, httpURI, httpBody, signature);

        FederatedSiteManagerDo siteByPartyId = federatedSiteManagerMapper.findSiteByPartyId(Long.parseLong(partyId), appKey, 2);

        if (siteByPartyId == null || 2 != siteByPartyId.getDetectiveStatus()) {
            return new CommonResponse(ReturnCodeEnum.SITE_STATUS_ERROR);
        }

        SiteDetailDto siteDetailDto = new SiteDetailDto(siteByPartyId);
        SecretInfo secretInfo = siteDetailDto.getSecretInfo();
        String key = secretInfo.getKey();
        String secret = secretInfo.getSecret();
        if (key == null || secret == null) {
            return new CommonResponse(ReturnCodeEnum.SECRET_ERROR);
        }
        Integer roleFromDataBase = siteDetailDto.getRole();

        String headSignature = EncryptUtil.generateSignature(secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);
        String trueSignature = EncryptUtil.generateSignature(secret, partyId, Integer.toString(roleFromDataBase), key, timestamp, nonce, httpURI, httpBody);

        log.info("Request Signature: {}", signature);
        log.info("Head Signature: {} | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", headSignature, secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);
        log.info("True Signature: {} | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", trueSignature, secret, partyId, siteDetailDto.getRole(), key, timestamp, nonce, httpURI, httpBody);

        if (signature.equals(headSignature) && signature.equals(trueSignature)) {
            return new CommonResponse(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
    }

    public CommonResponse heart(Long partyId) {

        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("party_id", partyId).eq("status", 2);
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setDetectiveStatus(2);
        federatedSiteManagerDo.setLastDetectiveTime(new Date());
        int update = federatedSiteManagerMapper.update(federatedSiteManagerDo, ew);
        if (update == 0) {
            return new CommonResponse(ReturnCodeEnum.UPDATE_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void updateSiteStatus() {
        log.info("start detective");
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.select("id", "last_detective_time");
        ew.eq("status", 2).eq("detective_status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(ew);
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            long time = new Date().getTime();
            Date lastDetectiveTime = federatedSiteManagerDo.getLastDetectiveTime();
            if (time - lastDetectiveTime.getTime() > 60000) {
                FederatedSiteManagerDo federatedSiteManagerDoToUpdate = new FederatedSiteManagerDo();
                federatedSiteManagerDoToUpdate.setDetectiveStatus(1);
                QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
                federatedSiteManagerDoQueryWrapper.eq("id", federatedSiteManagerDo.getId());
                federatedSiteManagerMapper.update(federatedSiteManagerDoToUpdate, ew);
            }
        }
    }

    public Boolean updateVersion(Long partyId, String appKey, VersionUpdateQo versionUpdateQo) {

        Preconditions.checkArgument(!StringUtils.isAllBlank(versionUpdateQo.getFateServingVersion(), versionUpdateQo.getFateVersion(), versionUpdateQo.getComponentVersion()));

        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("party_id", partyId);
        ew.like("secret_info", appKey);
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setFateServingVersion(versionUpdateQo.getFateServingVersion());
        federatedSiteManagerDo.setFateVersion(versionUpdateQo.getFateVersion());
        federatedSiteManagerDo.setComponentVersion(versionUpdateQo.getComponentVersion());

        if (federatedSiteManagerMapper.update(federatedSiteManagerDo, ew) > 0) {
            return true;
        }
        return false;
    }

    public boolean checkSiteName(String siteName) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.select("id").eq("site_name", siteName).in("status", 1, 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(ew);
        return federatedSiteManagerDos.size() > 0;
    }

    public boolean checkSiteName(SiteNameQo siteNameQo) {
        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.select("id").eq("site_name", siteNameQo.getSiteName()).in("status", 1, 2).ne(siteNameQo.getId() != null, "id", siteNameQo.getId());
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(ew);
        return federatedSiteManagerDos.size() > 0;
    }

    public boolean checkPartyIdInRegionNew(PartyIdInRegionCheckQo partyIdInRegionCheckQo) {
        Long partyId = partyIdInRegionCheckQo.getPartyId();
        List<Region> regions = partyIdInRegionCheckQo.getRegions();
        for (Region region : regions) {
            if (partyId >= region.getLeftRegion() && partyId <= region.getRightRegion()) {
                return true;
            }
        }
        return false;

    }

    public PageBean<InstitutionsDto> findInstitutions(InstitutionQo institutionQo) {
        //get parameters for page
//        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
//        federatedFateManagerUserDoQueryWrapper.eq("status", 2);
//        Integer institutionsCount = federatedFateManagerUserMapper.selectCount(federatedFateManagerUserDoQueryWrapper);
//        PageBean<InstitutionsDto> institutionsDtoPageBean = new PageBean<>(institutionQo.getPageNum(), institutionQo.getPageSize(), institutionsCount);
//        long startIndex = institutionsDtoPageBean.getStartIndex();



        if (StringUtils.isNotBlank(institutionQo.getCondition())){
            institutionQo.setCondition("%" + institutionQo.getCondition() + "%");
        }
        long count = federatedSiteManagerMapper.countForInstitutions(institutionQo);
        PageBean<InstitutionsDto> institutionsDtoPageBean = new PageBean<>(institutionQo.getPageNum(), institutionQo.getPageSize(), count);
        long startIndex = institutionsDtoPageBean.getStartIndex();

        //get paged institutions and their numbers

        List<InstitutionsDto> institutions = federatedSiteManagerMapper.findInstitutions(institutionQo, startIndex);
        institutionsDtoPageBean.setList(institutions);

        return institutionsDtoPageBean;

    }


    public PageBean<SiteDetailDto> findPagedSitesForFateManager(SiteListForFateManagerQo siteListForFateManagerQo) {
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.eq("institutions", siteListForFateManagerQo.getInstitutions()).eq("status", 2);
        long sitesCount = federatedSiteManagerMapper.selectCount(federatedSiteManagerDoQueryWrapper);

        PageBean<SiteDetailDto> siteDetailDtoPageBean = new PageBean<>(siteListForFateManagerQo.getPageNum(), siteListForFateManagerQo.getPageSize(), sitesCount);
        long startIndex = siteDetailDtoPageBean.getStartIndex();
        List<FederatedSiteManagerDo> pagedSites = federatedSiteManagerMapper.findPagedSitesForFateManager(siteListForFateManagerQo, startIndex);
        ArrayList<SiteDetailDto> siteDetailDtos = new ArrayList<>();
        for (int i = 0; i < pagedSites.size(); i++) {
            SiteDetailDto siteDetailDto = new SiteDetailDto(pagedSites.get(i));
            siteDetailDtos.add(siteDetailDto);
        }
        siteDetailDtoPageBean.setList(siteDetailDtos);
        return siteDetailDtoPageBean;
    }

    public List<String> findInstitutionsInGroup(InstitutionsInGroup institutionsInGroup) {
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        List<String> institutionsList = federatedSiteManagerMapper.findInstitutionsInGroup(institutionsInGroup);
        return institutionsList;
    }
}
