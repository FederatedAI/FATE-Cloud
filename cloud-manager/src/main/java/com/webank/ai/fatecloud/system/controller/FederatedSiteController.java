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
package com.webank.ai.fatecloud.system.controller;

import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedIpManagerDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedIpManagerServiceFacade;
import com.webank.ai.fatecloud.system.service.facade.FederatedSiteManagerServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;


@Slf4j
@RestController
@Data
@RequestMapping("/api/site")
@Api(tags = "FederatedSiteController", description = "Federation Site Page")
public class FederatedSiteController {

    @Autowired
    FederatedSiteManagerServiceFacade federatedSiteManagerServiceFacade;

    @Autowired
    FederatedIpManagerServiceFacade federatedIpManagerServiceFacade;

    @PostMapping(value = "/check")
    @ApiOperation(value = "Check Site")
    public CommonResponse checkSite(@RequestBody PartyIdQo partyId) {
        log.info("RequestBody:{}", partyId);
        return federatedSiteManagerServiceFacade.checkPartyIdExist(partyId);
    }

    @PostMapping(value = "/checkSiteName")
    @ApiOperation(value = "Check Site name")
    public CommonResponse checkSiteName(@RequestBody SiteNameQo siteNameQo) {
        log.info("RequestBody:{}", siteNameQo);
        return federatedSiteManagerServiceFacade.checkSiteName(siteNameQo);
    }

    @PostMapping(value = "/checkPartyId")
    @ApiOperation(value = "Check PartyId")
    public CommonResponse checkPartyId(@RequestBody SitePartyIdCheckQo sitePartyIdCheckQo) {
        log.info("RequestBody:{}", sitePartyIdCheckQo);
        return federatedSiteManagerServiceFacade.checkPartyId(sitePartyIdCheckQo);
    }


    @PostMapping(value = "/find")
    @ApiOperation(value = "Party Id Detail")
    public CommonResponse<SiteDetailDto> findSite(@RequestBody OneSiteQo oneSiteQo) {
        log.info("RequestBody:{}", oneSiteQo);
        return federatedSiteManagerServiceFacade.findSite(oneSiteQo.getId());
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "Delete Site From Federation")
    public CommonResponse deleteSite(@RequestBody OneSiteQo oneSiteQo) {
        log.info("RequestBody:{}", oneSiteQo);
        return federatedSiteManagerServiceFacade.deleteSite(oneSiteQo.getId());
    }


    @PostMapping(value = "/addNew")
    @ApiOperation(value = "Add Site To Federation")
    public CommonResponse<Long> addSiteNew(@RequestBody SiteAddQo siteAddQo) throws UnsupportedEncodingException {
        log.info("RequestBody:{}", siteAddQo);
        return federatedSiteManagerServiceFacade.addSiteNew(siteAddQo);

    }

    @PostMapping(value = "/updateNew")
    @ApiOperation(value = "Update Site")
    public CommonResponse updateSiteNew(@RequestBody SiteUpdateQo siteUpdateQo) throws UnsupportedEncodingException {
        log.info("RequestBody:{}", siteUpdateQo);
        return federatedSiteManagerServiceFacade.updateSiteNew(siteUpdateQo);
    }

    @PostMapping(value = "/page/cloudManager")
    @ApiOperation(value = "find all sites by page")
    public CommonResponse<PageBean<SiteDetailDto>> findPagedSites(@RequestBody SiteListQo siteListQo) {
        log.info("RequestBody:{}", siteListQo);
        return federatedSiteManagerServiceFacade.findPagedSites(siteListQo);
    }

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "find all the institutions and its site's number")
    public CommonResponse<PageBean<InstitutionsDto>> findInstitutions(@RequestBody InstitutionQo institutionQo) {
        return federatedSiteManagerServiceFacade.findInstitutions(institutionQo);
    }

    @PostMapping(value = "/checkWeb")
    @ApiOperation(value = "check the web")
    public CommonResponse checkWeb(@RequestBody TelnetIpQo telnetIpQo) {
        log.info("RequestBody:{}", telnetIpQo);
        return federatedSiteManagerServiceFacade.checkWeb(telnetIpQo);
    }

    @PostMapping(value = "/usedPage")
    @ApiOperation(value = "find used sites")
    public CommonResponse<PageBean<UsedSiteDto>> findUsedSites(@RequestBody UsedSiteListQo usedSiteListQo) {
        log.info("RequestBody:{}", usedSiteListQo);
        return federatedSiteManagerServiceFacade.findUsedSites(usedSiteListQo);
    }

    @PostMapping(value = "/institutionsInGroup")
    @ApiOperation(value = "find used institutions in group")
    public CommonResponse<List<String>> findInstitutionsInGroup(@RequestBody InstitutionsInGroup institutionsInGroup) {
        log.info("RequestBody:{}", institutionsInGroup);
        return federatedSiteManagerServiceFacade.findInstitutionsInGroup(institutionsInGroup);
    }

    @PostMapping(value = "/cloudManager/network")
    @ApiOperation(value = "find default network of cloud manager")
    public CommonResponse<NetworkDto> findCloudManagerNetwork() {
        return federatedSiteManagerServiceFacade.findCloudManagerNetwork();
    }
    @PostMapping(value = "/find/all")
    @ApiOperation(value = "find all site")
    public CommonResponse<List<Long>> findAllSite(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        return federatedSiteManagerServiceFacade.findAllSite(authorityApplyDetailsQo);
    }

    //interface for fate-manager

    @PostMapping(value = "/checkUrl")
    @ApiOperation(value = "checkUrl of Site for fate manager")
    public CommonResponse checkUrl(@RequestBody SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        log.info("siteActivateQo:{}", siteActivateQo);
        CommonResponse commonResponse = federatedSiteManagerServiceFacade.checkUrl(siteActivateQo, httpServletRequest);
        log.info("response for check url :{}",commonResponse);
        return commonResponse;
    }

    @PostMapping(value = "/activate")
    @ApiOperation(value = "activate Site for fate manager")
    public CommonResponse activateSite(@RequestBody SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        log.info("siteActivateQo:{}", siteActivateQo);
        CommonResponse commonResponse = federatedSiteManagerServiceFacade.activateSite(siteActivateQo, httpServletRequest);
        log.info("response for activate url :{}",commonResponse);
        return commonResponse;
    }


    @PostMapping(value = "/findOneSite/fateManager")
    @ApiOperation(value = "find site info for fate manager ")
    public CommonResponse<SiteDetailDto> findOneSite(HttpServletRequest httpServletRequest) {
        log.info("httpServletRequest for find one site:{}", httpServletRequest);
        CommonResponse<SiteDetailDto> oneSite = federatedSiteManagerServiceFacade.findOneSite(httpServletRequest);
        log.info("response for find one site:{}",oneSite);
        return oneSite;
    }


    @PostMapping(value = "/checkAuthority/fateManager")
    @ApiOperation(value = "check site authority in http body for fate manager")
    public CommonResponse checkSiteAuthority(@RequestBody String siteInfo, HttpServletRequest httpServletRequest) {
        log.info("site info for checkAuthority:{}", siteInfo);
        CommonResponse commonResponse = federatedSiteManagerServiceFacade.checkSiteAuthority(siteInfo, httpServletRequest);
        log.info("response for checkAuthority:{}",commonResponse);
        return commonResponse;
    }

//    @PostMapping(value = "/heart/fateManager")
//    @ApiOperation(value = "heart check for fate manager")
//    public CommonResponse heart(HttpServletRequest httpServletRequest) {
//        log.info("RequestBody:{}", httpServletRequest);
//        return federatedSiteManagerServiceFacade.heart(httpServletRequest);
//    }

    @PostMapping(value = "/ip/list")
    @ApiOperation(value = "Ip Manager")
    public CommonResponse<PageBean<IpManagerListDto>> selectIpList(@RequestBody IpManagerListQo ipManagerListQo) {
        log.info("ipManagerListQo for ip list:{}", ipManagerListQo);
        return federatedIpManagerServiceFacade.getIpList(ipManagerListQo);
    }

    @PostMapping(value = "/ip/deal")
    @ApiOperation(value = "Deal Ip Modify Apply")
    public CommonResponse dealIpModify(@RequestBody IpManagerUpdateQo ipManagerUpdateQo) {
        log.info("ipManagerUpdateQo for ip deal:{}", ipManagerUpdateQo);
        return federatedIpManagerServiceFacade.dealIpModify(ipManagerUpdateQo);
    }

    @PostMapping(value = "/ip/accept")
    @ApiOperation(value = "Accept Ip Modify Apply for fate manager")
    public CommonResponse<IpManagerAcceptDto> acceptIpModify(@RequestBody IpManagerAcceptQo ipManagerAcceptQo, HttpServletRequest httpServletRequest) {
        log.info("ipManagerAcceptQo for ip accept:{}", ipManagerAcceptQo);
        CommonResponse<IpManagerAcceptDto> ipManagerAcceptDtoCommonResponse = federatedIpManagerServiceFacade.acceptIpModify(ipManagerAcceptQo, httpServletRequest);
        log.info("response for ip accept:{}",ipManagerAcceptDtoCommonResponse);
        return ipManagerAcceptDtoCommonResponse;
    }

    @PostMapping(value = "/ip/query")
    @ApiOperation(value = "query Ip Modify Process for fate manager")
    public CommonResponse<IpManagerQueryDto> queryIpModify(@RequestBody IpManagerQueryQo ipManagerQueryQo, HttpServletRequest httpServletRequest) {
        log.info("ipManagerQueryQo for ip query:{}", ipManagerQueryQo);
        CommonResponse<IpManagerQueryDto> ipManagerQueryDtoCommonResponse = federatedIpManagerServiceFacade.queryIpModify(ipManagerQueryQo, httpServletRequest);
        log.info("response for ip query:{}",ipManagerQueryDtoCommonResponse);
        return ipManagerQueryDtoCommonResponse;
    }

    @PostMapping(value = "/ip/query/history")
    @ApiOperation(value = "query Ip Modify history")
    public CommonResponse<List<FederatedIpManagerDo>> queryIpModifyHistory(@RequestBody HistoryQo historyQo) {
        log.info("historyQo for ip history:{}", historyQo);
        return federatedIpManagerServiceFacade.queryIpModifyHistory(historyQo);
    }

    @PostMapping(value = "/fate/version")
    @ApiOperation(value = "update fate and serving version for fate manager")
    public CommonResponse updateVersion(@RequestBody VersionUpdateQo versionUpdateQo, HttpServletRequest httpServletRequest) {
        log.info("versionUpdateQo for fate version:{}", versionUpdateQo);
        CommonResponse commonResponse = federatedSiteManagerServiceFacade.updateVersion(versionUpdateQo, httpServletRequest);
        log.info("response for fate version:{}",commonResponse);
        return commonResponse;
    }

    @PostMapping(value = "/checkPartyIdInRegionNew")
    @ApiOperation(value = "Check PartyId in regions")
    public CommonResponse checkPartyIdInRegionNew(@RequestBody PartyIdInRegionCheckQo partyIdInRegionCheckQo) {
        log.info("partyIdInRegionCheckQo for checking party id:{}", partyIdInRegionCheckQo);
        return federatedSiteManagerServiceFacade.checkPartyIdInRegionNew(partyIdInRegionCheckQo);
    }

    @PostMapping(value = "/page/fateManager")
    @ApiOperation(value = "find all sites by page for fate manager")
    public CommonResponse<PageBean<SiteDetailDto>> findPagedSitesForFateManager(@RequestBody SiteListForFateManagerQo siteListForFateManagerQo, HttpServletRequest httpServletRequest) {
        log.info("siteListForFateManagerQo for page:{}", siteListForFateManagerQo);
        CommonResponse<PageBean<SiteDetailDto>> pagedSitesForFateManager = federatedSiteManagerServiceFacade.findPagedSitesForFateManager(siteListForFateManagerQo, httpServletRequest);
        log.info("response for page:{}",pagedSitesForFateManager);
        return pagedSitesForFateManager;
    }

    @PostMapping(value = "/rollsite/checkPartyId")
    @ApiOperation(value = "Check PartyId for roll site")
    public CommonResponse checkPartyIdForRollSite(HttpServletRequest httpServletRequest) {
        log.info("request for check party id for roll site :{}", httpServletRequest);
        CommonResponse commonResponse = federatedSiteManagerServiceFacade.checkPartyIdForRollSite(httpServletRequest);
        log.info("response for check party id for roll site:{}",commonResponse);
        return commonResponse;
    }

    @PostMapping(value = "/institutions/all/dropdown")
    @ApiOperation(value = "find all institutions for drop down")
    public CommonResponse<InstitutionsDropdownDto> findAllInstitutionsForDropdown() {
        return federatedSiteManagerServiceFacade.findAllInstitutionsForDropdown();
    }
}
