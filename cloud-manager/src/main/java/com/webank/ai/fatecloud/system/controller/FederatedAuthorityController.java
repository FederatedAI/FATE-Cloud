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
@CrossOrigin
public class FederatedAuthorityController {

    @Autowired
    FederatedAuthorityServiceFacade federatedAuthorityServiceFacade;

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "find all the institutions for site")
    public CommonResponse<PageBean<InstitutionsForFateDto>> findInstitutionsForSite(@RequestBody AuthorityInstitutionsQo authorityInstitutionsQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findInstitutionsForSite(authorityInstitutionsQo, httpServletRequest);
    }

    @PostMapping(value = "/apply")
    @ApiOperation(value = "apply access-authority of other institutions for site")
    public CommonResponse applyForAuthorityOfInstitutions(@RequestBody AuthorityApplyQo authorityApplyQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.applyForAuthorityOfInstitutions(authorityApplyQo, httpServletRequest);
    }


    @PostMapping(value = "/applied")
    @ApiOperation(value = "find institutions applying for this institutions")
    public CommonResponse<List<String>> findAuthorizedInstitutions(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findAuthorizedInstitutions(authorityApplyResultsQo, httpServletRequest);
    }

    @PostMapping(value = "/results")
    @ApiOperation(value = "find all apply result for site")
    public CommonResponse<List<FederatedSiteAuthorityDo>> findResultsOfAuthorityApply(@RequestBody AuthorityApplyResultsQo authorityApplyResultsQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findResultsOfAuthorityApply(authorityApplyResultsQo, httpServletRequest);
    }

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
    public CommonResponse<PageBean<AuthorityHistoryDto>> findAuthorityHistoryOfFateManager(@RequestBody AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo, HttpServletRequest httpServletRequest) {

        return federatedAuthorityServiceFacade.findAuthorityHistoryOfFateManager(authorityHistoryOfFateManagerQo, httpServletRequest);
    }

}
