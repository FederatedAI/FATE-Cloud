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
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedAuthorityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FederatedAuthorityServiceFacade {

    @Autowired
    FederatedAuthorityService federatedAuthorityService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse<PageBean<InstitutionsForFateDto>> findInstitutionsForSite(AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityInstitutionsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        if (authorityInstitutionsQo.getInstitutions() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //check the type
        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitutionsQo.getInstitutions());

        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        if ("1".equals(scenarioType)) {
//            if (institutionsType == 4) {
//                log.error("{} doesn't have site,or undefine", authorityInstitutionsQo.getInstitutions());
//                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
//            }

        } else if ("2".equals(scenarioType) || "3".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {
                log.error("{} isn't guest or mix", authorityInstitutionsQo.getInstitutions());
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }

        } else {
            return new CommonResponse<>(ReturnCodeEnum.SCENARIO_ERROR);

        }

        PageBean<InstitutionsForFateDto> pagedInstitutions = federatedAuthorityService.findInstitutionsForSite(authorityInstitutionsQo, scenarioType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedInstitutions);
    }

    public CommonResponse<PageBean<InstitutionsForFateDto>> findApprovedInstitutions(AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityInstitutionsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        if (authorityInstitutionsQo.getInstitutions() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitutionsQo.getInstitutions());

        PageBean<InstitutionsForFateDto> approvedInstitutions = federatedAuthorityService.findApprovedInstitutions(authorityInstitutionsQo, scenarioType, institutionsType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, approvedInstitutions);
    }

    public CommonResponse applyForAuthorityOfInstitutions(AuthorityApplyQo authorityApplyQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityApplyQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        //check parameters
        String institutions = authorityApplyQo.getInstitutions();
        ArrayList<String> authorityInstitutions = authorityApplyQo.getAuthorityInstitutions();
        if (StringUtils.isBlank(institutions) || authorityInstitutions.size() <= 0) {//check null value
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        HashSet<String> set = new HashSet<>(authorityInstitutions);
        if (set.size() != authorityInstitutions.size()) {//check duplicated values
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //check the type
        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(institutions);

        if ("1".equals(scenarioType)) {

        } else if ("2".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {//check type of institution launching apply
                log.error("{} isn't guest or mix", institutions);
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            for (String authorityInstitution : authorityInstitutions) {//check type if institutions being applied
                int authorityInstitutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitution);
                if (authorityInstitutionsType != 3 && authorityInstitutionsType != 1) {
                    log.error("{} isn't guest or mix", authorityInstitutionsType);
                    return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
                }
            }

        } else if ("3".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {//check type of institution launching apply
                log.error("{} isn't guest or mix", institutions);
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            for (String authorityInstitution : authorityInstitutions) {//check type if institutions being applied
                int authorityInstitutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitution);
                if (authorityInstitutionsType != 2 && authorityInstitutionsType != 1) {
                    log.error("{} isn't host or mix", authorityInstitutionsType);
                    return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
                }
            }
        } else {
            return new CommonResponse<>(ReturnCodeEnum.SCENARIO_ERROR);
        }

        if (federatedAuthorityService.applyForAuthorityOfInstitutions(authorityApplyQo, scenarioType, institutionsType)) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_APPLY_ERROR);


    }

    public CommonResponse<List<AuthorityApplyStatusDto>> findAuthorityApplyStatus(AuthorityApplyStatusQo authorityApplyStatusQo) {
        ArrayList<String> institutionsList = authorityApplyStatusQo.getInstitutions();
        if (institutionsList.size() <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        List<AuthorityApplyStatusDto> authorityApplyStatus = federatedAuthorityService.findAuthorityApplyStatus(authorityApplyStatusQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyStatus);

    }

    public CommonResponse<AuthorityApplyDetailsDto> findAuthorityApplyDetails(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        if (StringUtils.isBlank(authorityApplyDetailsQo.getInstitutions())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        String scenarioType = federatedAuthorityService.getScenarioType();
        AuthorityApplyDetailsDto authorityApplyDetailsDto = federatedAuthorityService.findAuthorityApplyDetails(authorityApplyDetailsQo, scenarioType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyDetailsDto);

    }

    public CommonResponse<Set<String>> findCurrentAuthority(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        if (StringUtils.isBlank(authorityApplyDetailsQo.getInstitutions())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        String scenarioType = federatedAuthorityService.getScenarioType();
        Set<String> authorityApplyDetails = federatedAuthorityService.findCurrentAuthority(authorityApplyDetailsQo, scenarioType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyDetails);

    }

    public CommonResponse reviewAuthorityApplyDetails(AuthorityApplyReviewQo authorityApplyReviewQo) {
        String institutions = authorityApplyReviewQo.getInstitutions();
        Set<String> approvedInstitutionsList = authorityApplyReviewQo.getApprovedInstitutionsList();

        Preconditions.checkArgument(approvedInstitutionsList != null);

        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(institutions);

        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        if ("1".equals(scenarioType)) {

        } else if ("2".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {//check type of institution launching apply
                log.error("{} isn't guest or mix", institutions);
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            for (String authorityInstitution : approvedInstitutionsList) {//check type if institutions being applied
                int authorityInstitutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitution);
                if (authorityInstitutionsType != 3 && authorityInstitutionsType != 1) {
                    log.error("{} isn't guest or mix", authorityInstitutionsType);
                    return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
                }
            }

        } else if ("3".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {//check type of institution launching apply
                log.error("{} isn't guest or mix", institutions);
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            for (String authorityInstitution : approvedInstitutionsList) {//check type if institutions being applied
                int authorityInstitutionsType = federatedAuthorityService.getInstitutionsType(authorityInstitution);
                if (authorityInstitutionsType != 2 && authorityInstitutionsType != 1) {
                    log.error("{} isn't host or mix", authorityInstitutionsType);
                    return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
                }
            }
        } else {
            return new CommonResponse<>(ReturnCodeEnum.SCENARIO_ERROR);
        }


        federatedAuthorityService.reviewAuthorityApplyDetails(authorityApplyReviewQo, scenarioType, institutionsType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistory(AuthorityHistoryQo authorityHistoryQo) {

        PageBean<AuthorityHistoryDto> authorityHistory = federatedAuthorityService.findAuthorityHistory(authorityHistoryQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityHistory);

    }

    public CommonResponse<List<FederatedSiteAuthorityDo>> findResultsOfAuthorityApply(AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {


        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityApplyResultsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }


        List<FederatedSiteAuthorityDo> resultsOfAuthorityApply = federatedAuthorityService.findResultsOfAuthorityApply(authorityApplyResultsQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, resultsOfAuthorityApply);

    }

    public CommonResponse<Set<String>> findAuthorizedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityApplyResultsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        if (StringUtils.isBlank(authorityApplyResultsQo.getInstitutions())) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //check the type
        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(authorityApplyResultsQo.getInstitutions());
        Set<String> authorizedInstitutions = federatedAuthorityService.findAuthorizedInstitutions(authorityApplyResultsQo, scenarioType, institutionsType);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorizedInstitutions);

    }


    public CommonResponse cancelAuthority(CancelListQo cancelListQo) {
        Preconditions.checkArgument(cancelListQo.getInstitutions() != null);

        if (cancelListQo.getAll().size() <= 0 && cancelListQo.getGuestList().size() <= 0 && cancelListQo.getHostList().size() <= 0) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(cancelListQo.getInstitutions());

        if (federatedAuthorityService.cancelAuthority(cancelListQo, scenarioType, institutionsType)) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }

        return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
    }

    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistoryOfFateManager(AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(authorityHistoryOfFateManagerQo.getInstitutions()));

//        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityHistoryOfFateManagerQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
//
//        if (!result) {
//            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
//        }

        PageBean<AuthorityHistoryDto> authorityHistory = federatedAuthorityService.findAuthorityHistoryOfFateManager(authorityHistoryOfFateManagerQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityHistory);

    }


    public CommonResponse<Boolean> checkPartyIdAuthority(PartyIdCheckQo partyIdCheckQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(partyIdCheckQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        if (StringUtils.isBlank(partyIdCheckQo.getInstitutions()) || StringUtils.isBlank(String.valueOf(partyIdCheckQo.getPartyId()))) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        //check the type
        //get the site-authority scenario, 1.mix 2.homo 3.hetero
        //get the institutions type 1.mix 2.host 3.guest 4.undefine
        String scenarioType = federatedAuthorityService.getScenarioType();
        int institutionsType = federatedAuthorityService.getInstitutionsType(partyIdCheckQo.getInstitutions());
        Boolean authorityResult = federatedAuthorityService.checkPartyIdAuthority(partyIdCheckQo, scenarioType, institutionsType);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityResult);

    }

    public CommonResponse<CancelListDto> findCancelList(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        if (StringUtils.isBlank(authorityApplyDetailsQo.getInstitutions())) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        String scenarioType = federatedAuthorityService.getScenarioType();
        if ("1".equals(scenarioType) || "2".equals(scenarioType) || "3".equals(scenarioType)) {
            int institutionsType = federatedAuthorityService.getInstitutionsType(authorityApplyDetailsQo.getInstitutions());
            CancelListDto cancelListDto = federatedAuthorityService.findCancelList(authorityApplyDetailsQo, scenarioType, institutionsType);
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS, cancelListDto);
        } else {
            return new CommonResponse(ReturnCodeEnum.SCENARIO_ERROR);
        }
    }
}
