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


import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityApplyStatusDto;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsForFateDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedAuthorityServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@Data
@RequestMapping("/api/authority")
@Api(tags = "FederatedAuthorityController", description = "Authority information for site")
public class FederatedAuthorityController {

    @Autowired
    FederatedAuthorityServiceFacade federatedAuthorityServiceFacade;

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "find all the institutions for site")
    public CommonResponse<PageBean<InstitutionsForFateDto>> findInstitutionsForSite(@RequestBody AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {
//        log.warn("findInstitutionsForSite:{}",authorityInstitutionsQo);
//         log.warn("findInstitutionsForSite:{}",federatedAuthorityServiceFacade.findInstitutionsForSite(authorityInstitutionsQo, httpServletRequest));

        return federatedAuthorityServiceFacade.findInstitutionsForSite(authorityInstitutionsQo, httpServletRequest);
    }

    @PostMapping(value = "/institutions/approved")
    @ApiOperation(value = "find all the institutions for site")
    public CommonResponse<PageBean<InstitutionsForFateDto>> findApprovedInstitutions(@RequestBody AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {
//        log.warn("findApprovedInstitutions:{}",authorityInstitutionsQo);
//        log.warn("findApprovedInstitutions:{}",federatedAuthorityServiceFacade.findApprovedInstitutions(authorityInstitutionsQo, httpServletRequest));
        return federatedAuthorityServiceFacade.findApprovedInstitutions(authorityInstitutionsQo, httpServletRequest);
    }

    @PostMapping(value = "/apply")
    @ApiOperation(value = "apply access-authority of other institutions for site")
    public CommonResponse applyForAuthorityOfInstitutions(@RequestBody AuthorityApplyQo authorityApplyQo, HttpServletRequest httpServletRequest) {
//        log.warn("applyForAuthorityOfInstitutions:{}",authorityApplyQo);
//        log.warn("applyForAuthorityOfInstitutions:{}",federatedAuthorityServiceFacade.applyForAuthorityOfInstitutions(authorityApplyQo, httpServletRequest));

        return federatedAuthorityServiceFacade.applyForAuthorityOfInstitutions(authorityApplyQo, httpServletRequest);
    }


    @PostMapping(value = "/applied")
    @ApiOperation(value = "find institutions applying for this institutions")
    public CommonResponse<List<String>> findAuthorizedInstitutions(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {
//        log.warn("findAuthorizedInstitutions:{}",authorityApplyResultsQo);
//        log.warn("findAuthorizedInstitutions:{}",federatedAuthorityServiceFacade.findAuthorizedInstitutions(authorityApplyResultsQo, httpServletRequest));
        return federatedAuthorityServiceFacade.findAuthorizedInstitutions(authorityApplyResultsQo, httpServletRequest);
    }

//    @PostMapping(value = "/results")
//    @ApiOperation(value = "find all apply result for site")
//    public CommonResponse<List<FederatedSiteAuthorityDo>> findResultsOfAuthorityApply(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {
//
//        return federatedAuthorityServiceFacade.findResultsOfAuthorityApply(authorityApplyResultsQo, httpServletRequest);
//    }

    @PostMapping(value = "/status")
    @ApiOperation(value = "find apply status from site")
    public CommonResponse<List<AuthorityApplyStatusDto>> findAuthorityApplyStatus(@RequestBody AuthorityApplyStatusQo authorityApplyStatusQo) {

        return federatedAuthorityServiceFacade.findAuthorityApplyStatus(authorityApplyStatusQo);
    }

    @PostMapping(value = "/details")
    @ApiOperation(value = "find apply details for site")
    public CommonResponse<List<FederatedSiteAuthorityDo>> findAuthorityApplyDetails(@RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        return federatedAuthorityServiceFacade.findAuthorityApplyDetails(authorityApplyDetailsQo);
    }

    @PostMapping(value = "/currentAuthority")
    @ApiOperation(value = "find current authority details for the site")
    public CommonResponse<List<String>> findCurrentAuthority(@RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        return federatedAuthorityServiceFacade.findCurrentAuthority(authorityApplyDetailsQo);
    }


    @PostMapping(value = "/review")
    @ApiOperation(value = "review apply details for site")
    public CommonResponse reviewAuthorityApplyDetails(@RequestBody AuthorityApplyReviewQo authorityApplyReviewQo) {

        return federatedAuthorityServiceFacade.reviewAuthorityApplyDetails(authorityApplyReviewQo);
    }

    @PostMapping(value = "/cancel")
    @ApiOperation(value = "cancel the authority")
    public CommonResponse cancelAuthority(@RequestBody CancelQo cancelQo) {

        return federatedAuthorityServiceFacade.cancelAuthority(cancelQo);
    }


    @PostMapping(value = "/history")
    @ApiOperation(value = "find authority history")
    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistory(@RequestBody AuthorityHistoryQo authorityHistoryQo) {

        return federatedAuthorityServiceFacade.findAuthorityHistory(authorityHistoryQo);
    }

    @PostMapping(value = "/history/fateManager")
    @ApiOperation(value = "find authority history of fateManager")
    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistoryOfFateManager(@RequestBody AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo) {

        return federatedAuthorityServiceFacade.findAuthorityHistoryOfFateManager(authorityHistoryOfFateManagerQo);
    }


    @PostMapping(value = "/check/partyId")
    @ApiOperation(value = "check institutions of the partyId has the authority of the institutions")
    public CommonResponse<Boolean> checkPartyIdAuthority(@RequestBody PartyIdCheckQo partyIdCheckQo, HttpServletRequest httpServletRequest) {
//        log.warn("checkPartyIdAuthority:{}",partyIdCheckQo);
//        log.warn("checkPartyIdAuthority:{}",federatedAuthorityServiceFacade.checkPartyIdAuthority(partyIdCheckQo, httpServletRequest));
        return federatedAuthorityServiceFacade.checkPartyIdAuthority(partyIdCheckQo,httpServletRequest);
    }

}
