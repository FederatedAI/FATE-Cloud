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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
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
import java.util.Set;

@Slf4j
@RestController
@Data
@RequestMapping("/api/authority")
@Api(tags = "FederatedAuthorityController", description = "Authority information for site")
public class FederatedAuthorityController {

    @Autowired
    FederatedAuthorityServiceFacade federatedAuthorityServiceFacade;

    @PostMapping(value = "/findPendingApply")
    @ApiOperation(value = "find pending apply request")
    public CommonResponse<List<String>> findPendingApply(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findPendingApply(authorityApplyResultsQo, httpServletRequest);

    }

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "find all institutions authority status for the input institutions: 1 could apply, 2 already apply successfully")
    public CommonResponse<PageBean<InstitutionsForFateDto>> findInstitutionsForSite(@RequestBody AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        return federatedAuthorityServiceFacade.findInstitutionsForSite(authorityInstitutionsQo, httpServletRequest);

    }

    @PostMapping(value = "/apply")
    @ApiOperation(value = "apply access-authority of other institutions")
    public CommonResponse applyForAuthorityOfInstitutions(@RequestBody AuthorityApplyQo authorityApplyQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        return federatedAuthorityServiceFacade.applyForAuthorityOfInstitutions(authorityApplyQo, httpServletRequest);

    }

    @PostMapping(value = "/institutions/approved")
    @ApiOperation(value = "find the finished apply results of the input institutions ")
    public CommonResponse<PageBean<InstitutionsForFateDto>> findApprovedInstitutions(@RequestBody AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        return federatedAuthorityServiceFacade.findApprovedInstitutions(authorityInstitutionsQo, httpServletRequest);

    }

    @PostMapping(value = "/applied")
    @ApiOperation(value = "find  institutions lists which get the authority of the input institutions")
    public CommonResponse<CancelListDto> findAuthorizedInstitutions(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findAuthorizedInstitutions(authorityApplyResultsQo, httpServletRequest);

    }

    @PostMapping(value = "/status")
    @ApiOperation(value = "find current applying request of institutions")
    public CommonResponse<List<AuthorityApplyStatusDto>> findAuthorityApplyStatus(@RequestBody AuthorityApplyStatusQo authorityApplyStatusQo) {

        return federatedAuthorityServiceFacade.findAuthorityApplyStatus(authorityApplyStatusQo);
    }

    @PostMapping(value = "/details")
    @ApiOperation(value = "find current apply details for the input institutions")
    public CommonResponse<AuthorityApplyDetailsDto> findAuthorityApplyDetails(@RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        return federatedAuthorityServiceFacade.findAuthorityApplyDetails(authorityApplyDetailsQo);
    }

    @PostMapping(value = "/review")
    @ApiOperation(value = "review apply details for site")
    public CommonResponse reviewAuthorityApplyDetails(@RequestBody AuthorityApplyReviewQo authorityApplyReviewQo) {

        return federatedAuthorityServiceFacade.reviewAuthorityApplyDetails(authorityApplyReviewQo);
    }

    @PostMapping(value = "/currentAuthority")
    @ApiOperation(value = "find current apply institutions list for the input institutions")
    public CommonResponse<Set<String>> findCurrentAuthority(@RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        return federatedAuthorityServiceFacade.findCurrentAuthority(authorityApplyDetailsQo);
    }

    @PostMapping(value = "/cancelList")
    @ApiOperation(value = "find the institutions list to cancel")
    public CommonResponse<CancelListDto> findCancelList(@RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        return federatedAuthorityServiceFacade.findCancelList(authorityApplyDetailsQo);
    }

    @PostMapping(value = "/cancel")
    @ApiOperation(value = "cancel the authority")
    public CommonResponse cancelAuthority(@RequestBody CancelListQo cancelListQo) {

        return federatedAuthorityServiceFacade.cancelAuthority(cancelListQo);
    }


    @PostMapping(value = "/history")
    @ApiOperation(value = "find authority history")
    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistory(@RequestBody AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo) {

        return federatedAuthorityServiceFacade.findAuthorityHistory(authorityHistoryOfFateManagerQo);
    }

    @PostMapping(value = "/history/fateManager")
    @ApiOperation(value = "find authority history of fateManager")
    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistoryOfFateManager(@RequestBody AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        return federatedAuthorityServiceFacade.findAuthorityHistoryOfFateManager(authorityHistoryOfFateManagerQo, httpServletRequest);
    }

    @PostMapping(value = "/check/partyId")
    @ApiOperation(value = "check institutions of the partyId has the authority of the input institutions or not")
    public CommonResponse<Boolean> checkPartyIdAuthority(@RequestBody PartyIdCheckQo partyIdCheckQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        log.info("PartyIdCheckQo:{}", partyIdCheckQo);
        CommonResponse<Boolean> commonResponse = federatedAuthorityServiceFacade.checkPartyIdAuthority(partyIdCheckQo, httpServletRequest);
        log.info("PartyIdCheckResponse:{}", commonResponse);
        return commonResponse;
    }

}
