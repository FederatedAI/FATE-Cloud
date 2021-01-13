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
package com.webank.ai.fatecloud.system.service.facade;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupDetailDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsDto;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.dto.UsedSiteDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedGroupSetService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


@Slf4j
@Service
public class FederatedSiteManagerServiceFacade {

    @Autowired
    FederatedSiteManagerService federatedSiteManagerService;

    @Autowired
    FederatedGroupSetService federatedGroupSetService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse checkPartyIdExist(PartyIdQo partyIdQo) {
        if (partyIdQo.getPartyId() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (federatedSiteManagerService.checkPartyIdExist(partyIdQo)) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }


    public CommonResponse<PageBean<SiteDetailDto>> findPagedSites(SiteListQo siteListQo) {
        PageBean<SiteDetailDto> pagedSites = federatedSiteManagerService.findPagedSites(siteListQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedSites);
    }

    public CommonResponse<PageBean<SiteDetailDto>> findPagedSitesForFDN(SiteListQo siteListQo) {
        PageBean<SiteDetailDto> pagedSites = federatedSiteManagerService.findPagedSites(siteListQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedSites);
    }

    public CommonResponse<Long> addSite(SiteAddQo siteAddQo) throws UnsupportedEncodingException {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(siteAddQo.getSiteName(), String.valueOf(siteAddQo.getGroupId())), String.valueOf(siteAddQo.getPartyId()));
        boolean existSite = federatedSiteManagerService.checkSiteName(new SiteNameQo(siteAddQo));
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.SITE_NAME_ERROR);
        }
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetService.selecFederatedGroupSet(siteAddQo);
        if (federatedGroupSetDo == null) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_SET_ERROR);
        }
        String rangeInfo = federatedGroupSetDo.getRangeInfo();
        Long partyId = siteAddQo.getPartyId();
        SitePartyIdCheckQo sitePartyIdCheckQo = new SitePartyIdCheckQo(rangeInfo, partyId);
        CommonResponse commonResponse = checkPartyId(sitePartyIdCheckQo);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        if (federatedSiteManagerService.checkPartyIdExist(new PartyIdQo(siteAddQo))) {
            return new CommonResponse<>(ReturnCodeEnum.PARTYID_ERROR);
        }
        Long id = federatedSiteManagerService.addSite(siteAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, id);
    }

    public CommonResponse<SiteDetailDto> findSite(Long id) {
        SiteDetailDto site = federatedSiteManagerService.findSite(id);
        return site == null ? new CommonResponse<>(ReturnCodeEnum.PARTYID_FIND_ERROR) : new CommonResponse<>(ReturnCodeEnum.SUCCESS, site);
    }

    public CommonResponse deleteSite(Long id) {
        SiteDetailDto site = federatedSiteManagerService.findSite(id);
        if (site == null) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_FIND_ERROR);
        }
        if (3 == site.getStatus()) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_DELETE_ERROR);
        }
        federatedSiteManagerService.deleteSite(id);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse updateSite(SiteUpdateQo siteUpdateQo) throws UnsupportedEncodingException {
        if (siteUpdateQo.getId() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (siteUpdateQo.getSiteName() != null) {
            boolean existSite = federatedSiteManagerService.checkSiteName(new SiteNameQo(siteUpdateQo));
            if (existSite) {
                return new CommonResponse<>(ReturnCodeEnum.SITE_NAME_ERROR);
            }
        }
        if (siteUpdateQo.getPartyId() != null) {
            if (federatedSiteManagerService.checkPartyIdExist(new PartyIdQo(siteUpdateQo))) {
                return new CommonResponse(ReturnCodeEnum.PARTYID_ERROR);
            }
        }

        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetService.selecFederatedGroupSet(siteUpdateQo);
        if (federatedGroupSetDo == null) {
            return new CommonResponse(ReturnCodeEnum.GROUP_SET_ERROR);
        }
        SiteDetailDto site = federatedSiteManagerService.findSite(siteUpdateQo.getId());
        if (site == null || 1 != site.getStatus()) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_UPDATE__ERROR);
        }

        int checkResult = federatedSiteManagerService.checkPartyId(new SitePartyIdCheckQo(federatedGroupSetDo.getRangeInfo(), siteUpdateQo.getPartyId()));
        if (0 != checkResult) {
            return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR);
        }

        federatedSiteManagerService.updateSite(siteUpdateQo, site.getGroupId());
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkUrl(SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        if (siteActivateQo.getRegistrationLink() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }

//        boolean result = checkSignature.checkSignature(httpServletRequest, JSON.toJSONString(siteActivateQo), 1);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(siteActivateQo), Dict.FATE_SITE_USER, new int[]{2}, 1);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);

        }

//        SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)),1);
        FederatedSiteManagerDo site = federatedSiteManagerService.findSiteDoByPartyId(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)), 1);
        if (site == null) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_UPDATE__ERROR);
        }

        String registrationLink = site.getRegistrationLink();
        String finalUrl = registrationLink.replaceAll("[\\s*\t\n\r]", " ");
        String registrationLinkFromRequest = siteActivateQo.getRegistrationLink();

        log.info("registrationLinkFromRequest:{}", registrationLinkFromRequest);
        log.info("finalUrl                   :{}", finalUrl);

        if (!finalUrl.equals(registrationLinkFromRequest)) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_ACTIVATE__ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse activateSite(SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        CommonResponse commonResponse = checkUrl(siteActivateQo, httpServletRequest);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        federatedSiteManagerService.activateSite(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)));
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkPartyId(SitePartyIdCheckQo sitePartyIdCheckQo) {
        int result = federatedSiteManagerService.checkPartyId(sitePartyIdCheckQo);
        if (0 == result) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR);
    }

    public CommonResponse checkWeb(TelnetIpQo telnetIpQo) {
        boolean result = federatedSiteManagerService.checkWeb(telnetIpQo);
        if (result) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.CONNECT_SITE_ERROR);
    }

    public CommonResponse<PageBean<UsedSiteDto>> findUsedSites(UsedSiteListQo usedSiteListQo) {
        if (!"asc".equals(usedSiteListQo.getOrderRule()) && !"desc".equals(usedSiteListQo.getOrderRule())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (!Dict.ORDER_FIELDS.contains(usedSiteListQo.getOrderField())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        PageBean<UsedSiteDto> usedSites = federatedSiteManagerService.findUsedSites(usedSiteListQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, usedSites);
    }

    public CommonResponse checkSiteAuthority(String siteInfo, HttpServletRequest httpServletRequest) {
//        boolean result = checkSignature.checkSignature(httpServletRequest, siteInfo, 2);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, siteInfo, Dict.FATE_SITE_USER, new int[]{2}, 2);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        return federatedSiteManagerService.checkSiteAuthority(siteInfo);
    }

    public CommonResponse checkSiteAuthorityForFDN(String siteInfo, HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignature(httpServletRequest, siteInfo, 2);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        return federatedSiteManagerService.checkSiteAuthority(siteInfo);
    }


    public CommonResponse heart(HttpServletRequest httpServletRequest) {
//        boolean result = checkSignature.checkSignature(httpServletRequest, "", 2);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_SITE_USER, new int[]{2}, 2);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        return federatedSiteManagerService.heart(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)));
    }

    public CommonResponse heartForFDN(HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignature(httpServletRequest, "", 2);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        return federatedSiteManagerService.heart(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)));
    }

    public CommonResponse<SiteDetailDto> findOneSite(HttpServletRequest httpServletRequest) {
        //check authority
//        boolean result = checkSignature.checkSignature(httpServletRequest, "", 2, 3);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_SITE_USER, new int[]{2}, 2, 3);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        //check site in http body
        SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)), httpServletRequest.getHeader(Dict.APP_KEY), 2, 3);
        if (site == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARTYID_FIND_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, site);
    }

    public CommonResponse<SiteDetailDto> findOneSiteForFDN(HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignature(httpServletRequest, "", 2, 3);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        //check site in http body
        SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)), httpServletRequest.getHeader(Dict.APP_KEY), 2, 3);
        if (site == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARTYID_FIND_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, site);
    }

    public CommonResponse updateVersion(VersionUpdateQo versionUpdateQo, HttpServletRequest httpServletRequest) {
        //check authority
        String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
        boolean result;
        if (StringUtils.isNotBlank(fateManagerUserId)) {
            result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(versionUpdateQo), Dict.FATE_SITE_USER, new int[]{2}, 2);
        } else {
            result = checkSignature.checkSignature(httpServletRequest, JSON.toJSONString(versionUpdateQo), 2);
        }
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }


        result = federatedSiteManagerService.updateVersion(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)), httpServletRequest.getHeader(Dict.APP_KEY), versionUpdateQo);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.UPDATE_VERSION_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkSiteName(SiteNameQo siteNameQo) {
        boolean existSite = federatedSiteManagerService.checkSiteName(siteNameQo);
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.SITE_NAME_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkPartyIdInRegionNew(PartyIdInRegionCheckQo partyIdInRegionCheckQo) {
        if (partyIdInRegionCheckQo == null || partyIdInRegionCheckQo.getPartyId() == null || partyIdInRegionCheckQo.getRegions() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        boolean result = federatedSiteManagerService.checkPartyIdInRegionNew(partyIdInRegionCheckQo);
        if (result) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.PARTYID_RANGE__ERROR);
    }

    public CommonResponse<Long> addSiteNew(SiteAddQo siteAddQo) throws UnsupportedEncodingException {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(siteAddQo.getSiteName(), String.valueOf(siteAddQo.getGroupId())), String.valueOf(siteAddQo.getPartyId()));
        boolean existSite = federatedSiteManagerService.checkSiteName(new SiteNameQo(siteAddQo));
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.SITE_NAME_ERROR);
        }

        //check party id in group regions
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetService.selectGroupSetDetails(siteAddQo);
        if (federatedGroupSetDo == null) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_SET_ERROR);
        }

        List<FederatedGroupDetailDo> federatedGroupDetailDos = federatedGroupSetDo.getFederatedGroupDetailDos();
        LinkedList<Region> regions = new LinkedList<>();
        for (FederatedGroupDetailDo federatedGroupDetailDo : federatedGroupDetailDos) {
            Long leftRegion = federatedGroupDetailDo.getLeftRegion();
            Long rightRegion = federatedGroupDetailDo.getRightRegion();
            Region region = new Region();
            region.setLeftRegion(leftRegion);
            region.setRightRegion(rightRegion);
            regions.add(region);
        }
        PartyIdInRegionCheckQo partyIdInRegionCheckQo = new PartyIdInRegionCheckQo();
        partyIdInRegionCheckQo.setPartyId(siteAddQo.getPartyId());
        partyIdInRegionCheckQo.setRegions(regions);
        CommonResponse commonResponse = this.checkPartyIdInRegionNew(partyIdInRegionCheckQo);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        if (federatedSiteManagerService.checkPartyIdExist(new PartyIdQo(siteAddQo))) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_ERROR);
        }
        Long id = federatedSiteManagerService.addSite(siteAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, id);
    }

    public CommonResponse updateSiteNew(SiteUpdateQo siteUpdateQo) throws UnsupportedEncodingException {
        if (siteUpdateQo.getId() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (siteUpdateQo.getSiteName() != null) {
            boolean existSite = federatedSiteManagerService.checkSiteName(new SiteNameQo(siteUpdateQo));
            if (existSite) {
                return new CommonResponse<>(ReturnCodeEnum.SITE_NAME_ERROR);
            }
        }
        if (siteUpdateQo.getPartyId() != null) {
            if (federatedSiteManagerService.checkPartyIdExist(new PartyIdQo(siteUpdateQo))) {
                return new CommonResponse(ReturnCodeEnum.PARTYID_ERROR);
            }
        }

        SiteDetailDto site = federatedSiteManagerService.findSite(siteUpdateQo.getId());
        if (site == null || 1 != site.getStatus()) {
            return new CommonResponse(ReturnCodeEnum.PARTYID_UPDATE__ERROR);
        }


        //check party id in group regions
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetService.selectGroupSetDetails(siteUpdateQo);
        if (federatedGroupSetDo == null) {
            return new CommonResponse(ReturnCodeEnum.GROUP_SET_ERROR);
        }

        List<FederatedGroupDetailDo> federatedGroupDetailDos = federatedGroupSetDo.getFederatedGroupDetailDos();
        LinkedList<Region> regions = new LinkedList<>();
        for (FederatedGroupDetailDo federatedGroupDetailDo : federatedGroupDetailDos) {
            Long leftRegion = federatedGroupDetailDo.getLeftRegion();
            Long rightRegion = federatedGroupDetailDo.getRightRegion();
            Region region = new Region();
            region.setLeftRegion(leftRegion);
            region.setRightRegion(rightRegion);
            regions.add(region);
        }
        PartyIdInRegionCheckQo partyIdInRegionCheckQo = new PartyIdInRegionCheckQo();
        partyIdInRegionCheckQo.setPartyId(siteUpdateQo.getPartyId());
        partyIdInRegionCheckQo.setRegions(regions);
        CommonResponse commonResponse = this.checkPartyIdInRegionNew(partyIdInRegionCheckQo);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        federatedSiteManagerService.updateSite(siteUpdateQo, site.getGroupId());
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<InstitutionsDto>> findInstitutions(InstitutionQo institutionQo) {

        PageBean<InstitutionsDto> pagedInstitutions = federatedSiteManagerService.findInstitutions(institutionQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedInstitutions);
    }

    public CommonResponse<PageBean<SiteDetailDto>> findPagedSitesForFateManager(SiteListForFateManagerQo siteListForFateManagerQo, HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(siteListForFateManagerQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        PageBean<SiteDetailDto> pagedSites = federatedSiteManagerService.findPagedSitesForFateManager(siteListForFateManagerQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedSites);
    }


    public CommonResponse<List<String>> findInstitutionsInGroup(InstitutionsInGroup institutionsInGroup) {
        if (institutionsInGroup.getGroupId() == null || institutionsInGroup.getStatusList().length == 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        List<String> institutionsList = federatedSiteManagerService.findInstitutionsInGroup(institutionsInGroup);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, institutionsList);


    }

    public CommonResponse checkPartyIdForRollSite(HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignature(httpServletRequest, "", 1, 2);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        HashMap<String, Boolean> stringBooleanHashMap = new HashMap<>();
        if (federatedSiteManagerService.checkPartyIdForRollSite(Long.parseLong(httpServletRequest.getHeader(Dict.PARTY_ID)))) {
            stringBooleanHashMap.put("result", true);
        } else {
            stringBooleanHashMap.put("result", false);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, stringBooleanHashMap);
    }
}
