package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityApplyStatusDto;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsForFateDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedAuthorityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        PageBean<InstitutionsForFateDto> pagedInstitutions = federatedAuthorityService.findInstitutionsForSite(authorityInstitutionsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedInstitutions);
    }


    public CommonResponse applyForAuthorityOfInstitutions(AuthorityApplyQo authorityApplyQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityApplyQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }


        if (federatedAuthorityService.applyForAuthorityOfInstitutions(authorityApplyQo)) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_APPLY_ERROR);


    }

    public CommonResponse<List<AuthorityApplyStatusDto>> findAuthorityApplyStatus(AuthorityApplyStatusQo authorityApplyStatusQo) {

        List<AuthorityApplyStatusDto> authorityApplyStatus = federatedAuthorityService.findAuthorityApplyStatus(authorityApplyStatusQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyStatus);

    }

    public CommonResponse<List<FederatedSiteAuthorityDo>> findAuthorityApplyDetails(AuthorityApplyDetailsQo authorityApplyDetailsQo) {

        List<FederatedSiteAuthorityDo> authorityApplyDetails = federatedAuthorityService.findAuthorityApplyDetails(authorityApplyDetailsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyDetails);

    }

    public CommonResponse<List<String>> findCurrentAuthority(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        List<String> authorityApplyDetails = federatedAuthorityService.findCurrentAuthority(authorityApplyDetailsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorityApplyDetails);

    }

    public CommonResponse reviewAuthorityApplyDetails(AuthorityApplyReviewQo authorityApplyReviewQo) {
        Preconditions.checkArgument(authorityApplyReviewQo.getApprovedInstitutionsList() != null);
        federatedAuthorityService.reviewAuthorityApplyDetails(authorityApplyReviewQo);
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

    public CommonResponse<List<String>> findAuthorizedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityApplyResultsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        List<String> authorizedInstitutions = federatedAuthorityService.findAuthorizedInstitutions(authorityApplyResultsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, authorizedInstitutions);

    }


    public CommonResponse cancelAuthority(CancelQo cancelQo) {
        Preconditions.checkArgument(cancelQo.getCanceledInstitutionsList() != null);
        if (federatedAuthorityService.cancelAuthority(cancelQo)) {

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

    public CommonResponse<PageBean<InstitutionsForFateDto>> findApprovedInstitutions(AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {
        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityInstitutionsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        if (authorityInstitutionsQo.getInstitutions() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        PageBean<InstitutionsForFateDto> approvedInstitutions = federatedAuthorityService.findApprovedInstitutions(authorityInstitutionsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, approvedInstitutions);
    }


}
