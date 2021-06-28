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
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.*;
import com.webank.ai.fatecloud.system.dao.mapper.*;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class FederatedAuthorityService {

    @Autowired
    private FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    private FederatedSiteAuthorityMapper federatedSiteAuthorityMapper;

    @Autowired
    private FederatedFateManagerUserMapper federatedFateManagerUserMapper;

    @Autowired
    private FederatedFunctionMapper federatedFunctionMapper;

    @Autowired
    private FederatedGroupSetMapper federatedGroupSetMapper;


    public PageBean<InstitutionsForFateDto> findInstitutionsForSite(AuthorityInstitutionsQo authorityInstitutionsQo) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("status", "2").ne("institutions", authorityInstitutionsQo.getInstitutions());
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);
        long institutionsCount = federatedFateManagerUserDos.size();

//        PageBean<InstitutionsForFateDto> institutionsForFateDtoPageBean = new PageBean<>(authorityInstitutionsQo.getPageNum(), authorityInstitutionsQo.getPageSize(), institutionsCount);
        PageBean<InstitutionsForFateDto> institutionsForFateDtoPageBean;
        if (institutionsCount <= 0) {
            institutionsForFateDtoPageBean = new PageBean<>(1, 1, 0);
        } else {
            institutionsForFateDtoPageBean = new PageBean<>(1, institutionsCount, institutionsCount);
        }
//        long startIndex = institutionsForFateDtoPageBean.getStartIndex();

        //get institutions list
//        List<FederatedFateManagerUserDo> pagedInstitutions = federatedFateManagerUserMapper.findPagedInstitutions(authorityInstitutionsQo, startIndex);


        //get authority institutions for one site to apply
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        //federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityInstitutionsQo.getInstitutions()).in("status", 1, 2);
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityInstitutionsQo.getInstitutions()).in("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        ArrayList<String> authorityInstitutionsList = new ArrayList<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            authorityInstitutionsList.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }

        //get institutionsForFateDto list to return
        ArrayList<InstitutionsForFateDto> institutionsForFateDtos = new ArrayList<>();
        for (FederatedFateManagerUserDo federatedFateManagerUserDo : federatedFateManagerUserDos) {
            InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto(federatedFateManagerUserDo);
            if (authorityInstitutionsList.contains(federatedFateManagerUserDo.getInstitutions())) {
                institutionsForFateDto.setStatus(2);
            } else {
                institutionsForFateDto.setStatus(1);
            }

            institutionsForFateDtos.add(institutionsForFateDto);
        }

        institutionsForFateDtoPageBean.setList(institutionsForFateDtos);

        return institutionsForFateDtoPageBean;
    }

    @Transactional
    public boolean applyForAuthorityOfInstitutions(AuthorityApplyQo authorityApplyQo) {
        //check whether there are pending apply or not now
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForExistApply = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapperForExistApply.eq("institutions", authorityApplyQo.getInstitutions()).eq("status", 1).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDosForExistApply = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapperForExistApply);
        if (federatedSiteAuthorityDosForExistApply.size() > 0) {
            return false;
        }

        //check parameters
        ArrayList<String> authorityInstitutions = authorityApplyQo.getAuthorityInstitutions();

        //insert authority institutions
        Date date = new Date();
        Long maxSequence = federatedSiteAuthorityMapper.findMaxSequence();
        if (maxSequence == null) {
            maxSequence = 1L;
        } else {
            maxSequence++;
        }

        for (String authorityInstitution : authorityInstitutions) {
            //update generation status of old authority info
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyQo.getInstitutions()).eq("authority_institutions", authorityInstitution).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                FederatedSiteAuthorityDo federatedSiteAuthorityDo = federatedSiteAuthorityDos.get(0);
                if (federatedSiteAuthorityDo.getStatus() == 2 || federatedSiteAuthorityDo.getStatus() == 1) {
                    return false;
                } else {
                    FederatedSiteAuthorityDo federatedSiteAuthorityDoToUpdate = new FederatedSiteAuthorityDo();
                    federatedSiteAuthorityDoToUpdate.setGeneration(2);
                    federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoToUpdate, federatedSiteAuthorityDoQueryWrapper);
                }
            }


            //insert new authority info
            FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
            federatedSiteAuthorityDo.setInstitutions(authorityApplyQo.getInstitutions());
            federatedSiteAuthorityDo.setAuthorityInstitutions(authorityInstitution);
            federatedSiteAuthorityDo.setCreateTime(date);
            federatedSiteAuthorityDo.setUpdateTime(date);
            federatedSiteAuthorityDo.setStatus(1);
            federatedSiteAuthorityDo.setGeneration(1);
            federatedSiteAuthorityDo.setSequence(maxSequence);

            federatedSiteAuthorityMapper.insert(federatedSiteAuthorityDo);
        }
        return true;

    }

    public List<AuthorityApplyStatusDto> findAuthorityApplyStatus(AuthorityApplyStatusQo authorityApplyStatusQo) {
        return federatedSiteAuthorityMapper.findAuthorityApplyStatus(authorityApplyStatusQo);
    }


    public Set<String> findCurrentAuthority(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyDetailsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        HashSet<String> institutionsSet = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            institutionsSet.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }
        return institutionsSet;
    }

    @Transactional
    public void reviewAuthorityApplyDetails(AuthorityApplyReviewQo authorityApplyReviewQo) {
        String institutions = authorityApplyReviewQo.getInstitutions();
        Set<String> approvedInstitutionsList = authorityApplyReviewQo.getApprovedInstitutionsList();

        //get all pending authority apply for this input institution
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForReview = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapperForReview.eq("institutions", institutions).eq("status", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapperForReview);

        Date date = new Date();
        if (approvedInstitutionsList == null) { //if null, set all applying status to 3(reject)
            FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
            federatedSiteAuthorityDo.setStatus(3);
            federatedSiteAuthorityDo.setUpdateTime(date);
            federatedSiteAuthorityMapper.update(federatedSiteAuthorityDo, federatedSiteAuthorityDoQueryWrapperForReview);
        } else {
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
                if (approvedInstitutionsList.contains(federatedSiteAuthorityDo.getAuthorityInstitutions())) { // if contains,set status 2(approve) or set 3
                    federatedSiteAuthorityDo.setStatus(2);
                } else {
                    federatedSiteAuthorityDo.setStatus(3);
                }
                federatedSiteAuthorityDo.setUpdateTime(date);
                federatedSiteAuthorityMapper.updateById(federatedSiteAuthorityDo);

            }
        }
    }

    public List<FederatedSiteAuthorityDo> findResultsOfAuthorityApply(AuthorityApplyResultsQo authorityApplyResultsQo) {

        long maxAuthoritySequence = federatedSiteAuthorityMapper.findMaxAuthoritySequence(authorityApplyResultsQo.getInstitutions());

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyResultsQo.getInstitutions()).eq("sequence", maxAuthoritySequence);

        return federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
    }

    public Set<String> findAuthorizedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo) {

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("authority_institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        HashSet<String> institutionsListWithAuthority = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            institutionsListWithAuthority.add(federatedSiteAuthorityDo.getInstitutions());
        }
        return institutionsListWithAuthority;

    }

    @Transactional
    public boolean cancelAuthority(CancelQo cancelQo) {
        Set<String> canceledInstitutionsList = cancelQo.getCanceledInstitutionsList();
        Date date = new Date();
        Long maxSequence = federatedSiteAuthorityMapper.findMaxSequence();
        if (maxSequence == null) {
            maxSequence = 1L;
        } else {
            maxSequence++;
        }
        for (String institutionsToCancel : canceledInstitutionsList) {

            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.eq("institutions", cancelQo.getInstitutions()).eq("authority_institutions", institutionsToCancel).eq("generation", 1).eq("status", 2);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                //update old generation
                FederatedSiteAuthorityDo federatedSiteAuthorityDoOldGeneration = federatedSiteAuthorityDos.get(0);
                federatedSiteAuthorityDoOldGeneration.setGeneration(2);
                federatedSiteAuthorityMapper.updateById(federatedSiteAuthorityDoOldGeneration);

                //insert new item
                FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
                federatedSiteAuthorityDo.setInstitutions(cancelQo.getInstitutions());
                federatedSiteAuthorityDo.setAuthorityInstitutions(institutionsToCancel);
                federatedSiteAuthorityDo.setCreateTime(date);
                federatedSiteAuthorityDo.setUpdateTime(date);
                federatedSiteAuthorityDo.setStatus(4);
                federatedSiteAuthorityDo.setGeneration(1);
                federatedSiteAuthorityDo.setSequence(maxSequence);
                federatedSiteAuthorityMapper.insert(federatedSiteAuthorityDo);
            } else {
                return false;
            }
        }
        return true;

    }

    public PageBean<AuthorityHistoryDto> findAuthorityHistoryOfFateManager(AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo) {

        long authorityHistoryCount = federatedSiteAuthorityMapper.findAuthorityHistoryOfFateManagerCount(authorityHistoryOfFateManagerQo);

        PageBean<AuthorityHistoryDto> authorityHistoryDtoPageBean = new PageBean<>(authorityHistoryOfFateManagerQo.getPageNum(), authorityHistoryOfFateManagerQo.getPageSize(), authorityHistoryCount);
        long startIndex = authorityHistoryDtoPageBean.getStartIndex();

        List<AuthorityHistoryDto> authorityHistoryDtos = federatedSiteAuthorityMapper.findAuthorityHistoryOfFateManager(authorityHistoryOfFateManagerQo, startIndex);

        authorityHistoryDtoPageBean.setList(authorityHistoryDtos);
        return authorityHistoryDtoPageBean;

    }


    public Boolean checkPartyIdAuthority(PartyIdCheckQo partyIdCheckQo) {
        FederatedSiteAuthorityDo federatedSiteAuthorityDo = federatedSiteAuthorityMapper.checkPartyIdAuthority(partyIdCheckQo);
        if (federatedSiteAuthorityDo == null) {
            return false;
        }
        return true;
    }

    //get the site-authority scenario, 1.mix 2.homo 3.hetero
    public String getScenarioType() {
        QueryWrapper<FederatedFunctionDo> federatedFunctionDoQueryWrapper = new QueryWrapper<>();
        federatedFunctionDoQueryWrapper.eq("function_name", Dict.FUNCTIONS[1]);
        List<FederatedFunctionDo> federatedFunctionDos = federatedFunctionMapper.selectList(federatedFunctionDoQueryWrapper);
        FederatedFunctionDo federatedFunctionDo = federatedFunctionDos.get(0);
        return federatedFunctionDo.getDescriptions();
    }

    //get the institutions type 1.mix 2.host 3.guest 4.undefine
    public int getInstitutionsType(String institutions) {

        List<Integer> roles = federatedSiteManagerMapper.getInstitutionsType(institutions);
        int size = roles.size();
        if (size <= 0) {
            return 4;
        }
        if (size == 1) {
            if (roles.get(0) == 2) { //2->host
                return 2;
            }
            if (roles.get(0) == 1) { //1->guest
                return 3;
            }
            return 4;
        }
        return 1;

    }

    public PageBean<InstitutionsForFateDto> findInstitutionsForSite(AuthorityInstitutionsQo authorityInstitutionsQo, String scenarioType, int type) {

        String inputInstitutions = authorityInstitutionsQo.getInstitutions();
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("status", "2").ne("institutions", inputInstitutions);
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);

        List<FederatedFateManagerUserDo> institutionsList = new ArrayList<>();
        if ("1".equals(scenarioType)) {// add all institutions
            institutionsList = federatedFateManagerUserDos;
        }
        if ("2".equals(scenarioType)) {// add institution contains guest
            if (type == 1 || type == 3) {// check the input institution type
                for (FederatedFateManagerUserDo federatedFateManagerUserDo : federatedFateManagerUserDos) {
                    String institutions = federatedFateManagerUserDo.getInstitutions();
                    int institutionsType = getInstitutionsType(institutions);
                    if (institutionsType == 3 || institutionsType == 1) {
                        institutionsList.add(federatedFateManagerUserDo);
                    }
                }
            } else {
                log.error("{} isn't guest or mix", authorityInstitutionsQo.getInstitutions());
            }
        }
        if ("3".equals(scenarioType)) {// add institutions contains host
            if (type == 1 || type == 3) {// check the input institution type
                for (FederatedFateManagerUserDo federatedFateManagerUserDo : federatedFateManagerUserDos) {
                    String institutions = federatedFateManagerUserDo.getInstitutions();
                    int institutionsType = getInstitutionsType(institutions);
                    if (institutionsType == 2 || institutionsType == 1) {
                        institutionsList.add(federatedFateManagerUserDo);
                    }
                }
            } else {
                log.error("{} isn't guest or mix", authorityInstitutionsQo.getInstitutions());
            }
        }

        //build page been
        long institutionsCount = institutionsList.size();
        PageBean<InstitutionsForFateDto> institutionsForFateDtoPageBean;
        if (institutionsCount <= 0) {
            institutionsForFateDtoPageBean = new PageBean<>(1, 1, 0);
        } else {
            institutionsForFateDtoPageBean = new PageBean<>(1, institutionsCount, institutionsCount);
        }

        //get list of institutions which the input institution has accessible authority
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();

//        if ("1".equals(scenarioType) || "2".equals(scenarioType)) {
//            federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + inputInstitutions + "',authority_institutions,institutions) authority_institutions");
//            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", inputInstitutions).or().eq("authority_institutions", inputInstitutions))
//                    .in("status", 2).eq("generation", 1);
//        } else {
//            federatedSiteAuthorityDoQueryWrapper.select("authority_institutions");
//            federatedSiteAuthorityDoQueryWrapper.eq("institutions", inputInstitutions).in("status", 2).eq("generation", 1);
//        }

        federatedSiteAuthorityDoQueryWrapper.eq("institutions", inputInstitutions).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        HashSet<String> authorityInstitutionsList = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            authorityInstitutionsList.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }

        //get institutionsForFateDto list to return
        ArrayList<InstitutionsForFateDto> institutionsForFateDtos = new ArrayList<>();
        for (FederatedFateManagerUserDo federatedFateManagerUserDo : institutionsList) {
            InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto(federatedFateManagerUserDo);
            if (authorityInstitutionsList.contains(federatedFateManagerUserDo.getInstitutions())) {
                institutionsForFateDto.setStatus(2);
            } else {
                institutionsForFateDto.setStatus(1);
            }

            institutionsForFateDtos.add(institutionsForFateDto);
        }

        institutionsForFateDtoPageBean.setList(institutionsForFateDtos);

        return institutionsForFateDtoPageBean;

    }

    public PageBean<InstitutionsForFateDto> findApprovedInstitutions(AuthorityInstitutionsQo authorityInstitutionsQo) {
        String institutions = authorityInstitutionsQo.getInstitutions();
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).ne("status", 1).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        HashSet<InstitutionsForFateDto> institutionsForFateDtos = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
            institutionsForFateDto.setInstitutions(federatedSiteAuthorityDo.getAuthorityInstitutions());
            institutionsForFateDto.setStatus(federatedSiteAuthorityDo.getStatus());
            institutionsForFateDtos.add(institutionsForFateDto);
        }

        long institutionsCount = institutionsForFateDtos.size();
        PageBean<InstitutionsForFateDto> institutionsForFateDtoPageBean;
        if (institutionsCount <= 0) {
            institutionsForFateDtoPageBean = new PageBean<>(1, 1, 0);
        } else {
            institutionsForFateDtoPageBean = new PageBean<>(1, institutionsCount, institutionsCount);
        }
        institutionsForFateDtoPageBean.setList(new ArrayList<>(institutionsForFateDtos));
        return institutionsForFateDtoPageBean;

    }

    @Transactional
    public boolean applyForAuthorityOfInstitutions(AuthorityApplyQo authorityApplyQo, String scenarioType, int institutionsType) {
        String institutions = authorityApplyQo.getInstitutions();
        ArrayList<String> authorityInstitutions = authorityApplyQo.getAuthorityInstitutions();

        //check whether the input institutions is valid or not
        QueryWrapper<FederatedFateManagerUserDo> ewForFateManagerUser = new QueryWrapper<>();
        ewForFateManagerUser.eq("institutions", institutions).eq("status", 2);
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(ewForFateManagerUser);
        if (federatedFateManagerUserDos.size() <= 0) {
            return false;
        }
        for (String authorityInstitution : authorityInstitutions) {
            QueryWrapper<FederatedFateManagerUserDo> ewForFateManagerUserToApply = new QueryWrapper<>();
            ewForFateManagerUserToApply.eq("institutions", authorityInstitution).eq("status", 2);
            List<FederatedFateManagerUserDo> federatedFateManagerUserDosToApply = federatedFateManagerUserMapper.selectList(ewForFateManagerUserToApply);
            if (federatedFateManagerUserDosToApply.size() <= 0) {
                return false;
            }
        }

        //check whether the input institution has pending applying request or not
        QueryWrapper<FederatedSiteAuthorityDo> ewForApply = new QueryWrapper<>();
        ewForApply.eq("institutions", institutions).eq("status", 1).eq("generation", 1);
        Integer count = federatedSiteAuthorityMapper.selectCount(ewForApply);
        if (count > 0) {
            return false;
        }

        //check whether the institutions to apply  has been applied by other institutions or not now
/*        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForExistApply = new QueryWrapper<>();
        if ("1".equals(scenarioType) || "2".equals(scenarioType)) {
            federatedSiteAuthorityDoQueryWrapperForExistApply.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions");
            federatedSiteAuthorityDoQueryWrapperForExistApply.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions));
            federatedSiteAuthorityDoQueryWrapperForExistApply.in("status", 1, 2).eq("generation", 1);
        } else {
            federatedSiteAuthorityDoQueryWrapperForExistApply.eq("institutions", institutions).in("status", 1, 2).eq("generation", 1);
        }
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDosForExistApply = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapperForExistApply);
        if (federatedSiteAuthorityDosForExistApply.size() > 0) {
            HashSet<String> institutionsSet = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDosForExistApply) {
                institutionsSet.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
            }
            for (String authorityInstitution : authorityInstitutions) {
                if (institutionsSet.contains(authorityInstitution)) {
                    return false;
                }
            }
        }*/
        //check the institutions to apply are approved before
        QueryWrapper<FederatedSiteAuthorityDo> ewForApprovedApply = new QueryWrapper<>();
        ewForApprovedApply.eq("institutions", institutions).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> approvedSiteAuthorityDo = federatedSiteAuthorityMapper.selectList(ewForApprovedApply);
        HashSet<String> approvedInstitutionsSet = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : approvedSiteAuthorityDo) {
            approvedInstitutionsSet.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }

        for (String authorityInstitution : authorityInstitutions) {
            if (approvedInstitutionsSet.contains(authorityInstitution)) {
                return false;
            }
        }

        //insert authority institutions
        Date date = new Date();
        Long maxSequence = federatedSiteAuthorityMapper.findMaxSequence();
        if (maxSequence == null) {
            maxSequence = 1L;
        } else {
            maxSequence++;
        }

        for (String authorityInstitution : authorityInstitutions) {
            //update generation status of old authority info
/*            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            if ("1".equals(scenarioType) || "2".equals(scenarioType)) {
                federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", authorityInstitution)).or(k -> k.eq("institutions", authorityInstitution).eq("authority_institutions", institutions)));
                federatedSiteAuthorityDoQueryWrapper.eq("generation", 1);
            } else {
                federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).eq("authority_institutions", authorityInstitution).eq("generation", 1);
            }

            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                FederatedSiteAuthorityDo federatedSiteAuthorityDoToUpdate = new FederatedSiteAuthorityDo();
                federatedSiteAuthorityDoToUpdate.setGeneration(2);
                federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoToUpdate, federatedSiteAuthorityDoQueryWrapper);
            }*/

            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).eq("authority_institutions", authorityInstitution).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                FederatedSiteAuthorityDo federatedSiteAuthorityDoToUpdate = new FederatedSiteAuthorityDo();
                federatedSiteAuthorityDoToUpdate.setGeneration(2);
                federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoToUpdate, federatedSiteAuthorityDoQueryWrapper);
            }

            //insert new authority info
            FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
            federatedSiteAuthorityDo.setInstitutions(institutions);
            federatedSiteAuthorityDo.setAuthorityInstitutions(authorityInstitution);
            federatedSiteAuthorityDo.setCreateTime(date);
            federatedSiteAuthorityDo.setUpdateTime(date);
            federatedSiteAuthorityDo.setStatus(1);
            federatedSiteAuthorityDo.setGeneration(1);
            federatedSiteAuthorityDo.setSequence(maxSequence);
            federatedSiteAuthorityMapper.insert(federatedSiteAuthorityDo);
        }
        return true;
    }

    public Boolean checkPartyIdAuthority(PartyIdCheckQo partyIdCheckQo, String scenarioType, int institutionsType) {
        String institutions = partyIdCheckQo.getInstitutions();
        Long partyId = partyIdCheckQo.getPartyId();

        if ("2".equals(scenarioType) || "3".equals(scenarioType)) {
            if (institutionsType != 3 && institutionsType != 1) {
                log.error("{} isn't guest or mix", partyIdCheckQo.getInstitutions());
                return false;
            }
        } else if (!"1".equals(scenarioType)) {
            log.error("scenario type {} doesn't support", scenarioType);
            return false;
        }

        //confirm party id exist
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.eq("party_id", partyId).eq("status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        if (federatedSiteManagerDos.size() <= 0) {
            return false;
        }

        //get institutions and role information of the party id
        FederatedSiteManagerDo federatedSiteManagerDo = federatedSiteManagerDos.get(0);
        String institutionsOfPartyId = federatedSiteManagerDo.getInstitutions();
        Long groupId = federatedSiteManagerDo.getGroupId();
        FederatedGroupSetDo federatedGroupSetDo = federatedGroupSetMapper.selectById(groupId);
        Integer role = federatedGroupSetDo.getRole();

        //check the authority
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId);
        federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        if (federatedSiteAuthorityDos.size() <= 0) {
            return false;
        }
        if ("1".equals(scenarioType)) {//mix type, both see guest an host
            return true;
        }
        if ("2".equals(scenarioType)) {// only can see guest
            return role == 1;
        }
        if ("3".equals(scenarioType)) {// only can see host
            return role == 2;
        }
        return false;

   /*     if ("1".equals(scenarioType)) {//mix type, both see guest an host
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId)).or(k -> k.eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions)));
//            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId).or().eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                return true;
            } else {
                return false;
            }

        }
        if ("2".equals(scenarioType)) {// only can see guest
            if (role != 1) {
                return false;
            }
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId)).or(k -> k.eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions)));
//            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId).or().eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            if (federatedSiteAuthorityDos.size() > 0) {
                return true;
            } else {
                return false;
            }
        }
        if ("3".equals(scenarioType)) {
            // if institutions launch the authority
            QueryWrapper<FederatedSiteAuthorityDo> ewOfInstitutions = new QueryWrapper<>();
            ewOfInstitutions.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId);
            ewOfInstitutions.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> authorityOfInstitutions = federatedSiteAuthorityMapper.selectList(ewOfInstitutions);

            //if institutions of the party id launch the authority
            QueryWrapper<FederatedSiteAuthorityDo> ewOfPartyId = new QueryWrapper<>();
            ewOfPartyId.eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions);
            ewOfPartyId.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> authorityOfPartyId = federatedSiteAuthorityMapper.selectList(ewOfPartyId);

            if (authorityOfInstitutions.size() > 0 && authorityOfPartyId.size() > 0) {
                return true;
            }

            if (authorityOfInstitutions.size() > 0) {
                return role == 2;
            }

            if (authorityOfPartyId.size() > 0) {
                return role == 1;
            }
        }
        return false;*/

    }


    public AuthorityApplyDetailsDto findAuthorityApplyDetails(AuthorityApplyDetailsQo authorityApplyDetailsQo, String scenarioType) {
        LinkedList<String> authorityInstitutionsList = new LinkedList<>();
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.select("authority_institutions").eq("institutions", authorityApplyDetailsQo.getInstitutions()).eq("status", 1).groupBy("authority_institutions");
        List<FederatedSiteAuthorityDo> authorityApplyDetails = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        for (FederatedSiteAuthorityDo authorityApplyDetail : authorityApplyDetails) {
            authorityInstitutionsList.add(authorityApplyDetail.getAuthorityInstitutions());
        }

        AuthorityApplyDetailsDto authorityApplyDetailsDto = new AuthorityApplyDetailsDto();
        authorityApplyDetailsDto.setInstitutionsList(authorityInstitutionsList);
        authorityApplyDetailsDto.setScenarioType(scenarioType);
        return authorityApplyDetailsDto;
    }

    public Set<String> findCurrentAuthority(AuthorityApplyDetailsQo authorityApplyDetailsQo, String scenarioType) {
        String institutions = authorityApplyDetailsQo.getInstitutions();

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions");
        federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions));
        federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        HashSet<String> institutionsList = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            institutionsList.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }
        return institutionsList;

    }

    @Transactional
    public void reviewAuthorityApplyDetails(AuthorityApplyReviewQo authorityApplyReviewQo, String scenarioType, int institutionsType) {

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForReview = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapperForReview.eq("institutions", authorityApplyReviewQo.getInstitutions()).eq("status", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapperForReview);
        Date date = new Date();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            if (authorityApplyReviewQo.getApprovedInstitutionsList().contains(federatedSiteAuthorityDo.getAuthorityInstitutions())) {
                federatedSiteAuthorityDo.setStatus(2);
            } else {
                federatedSiteAuthorityDo.setStatus(3);
            }
            federatedSiteAuthorityDo.setUpdateTime(date);
            federatedSiteAuthorityMapper.updateById(federatedSiteAuthorityDo);

        }
    }

    public CancelListDto findCancelList(AuthorityApplyDetailsQo authorityApplyDetailsQo, String scenarioType) {
        String institutions = authorityApplyDetailsQo.getInstitutions();
        CancelListDto cancelListDto = new CancelListDto();

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions);
        federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        HashSet<String> all = new HashSet<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            String authorityInstitutions = federatedSiteAuthorityDo.getAuthorityInstitutions();
            all.add(authorityInstitutions);
        }
        if ("1".equals(scenarioType)) {//mix
            cancelListDto.setAll(all);
        }

        if ("2".equals(scenarioType)) {//guest
            cancelListDto.setGuestList(all);
        }

        if ("3".equals(scenarioType)) {//host
            cancelListDto.setHostList(all);
        }

        cancelListDto.setScenarioType(scenarioType);
        return cancelListDto;

    }

    @Transactional
    public boolean cancelAuthority(CancelListQo cancelListQo, String scenarioType) {
        String institutions = cancelListQo.getInstitutions();

        Date date = new Date();
        Long maxSequence = federatedSiteAuthorityMapper.findMaxSequence();
        if (maxSequence == null) {
            maxSequence = 1L;
        } else {
            maxSequence++;
        }
        HashSet<String> institutionsSet = new HashSet<>();
        if ("1".equals(scenarioType)) {//mix
            institutionsSet = cancelListQo.getAll();
        }

        if ("2".equals(scenarioType)) {//guest
            institutionsSet = cancelListQo.getGuestList();
        }

        if ("3".equals(scenarioType)) {//host
            institutionsSet = cancelListQo.getHostList();
        }
        for (String institutionsToCancel : institutionsSet) {
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).eq("authority_institutions", institutionsToCancel);
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);

            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

            if (federatedSiteAuthorityDos.size() > 0) {
                //update old generation
                FederatedSiteAuthorityDo federatedSiteAuthorityDoOldGeneration = new FederatedSiteAuthorityDo();
                federatedSiteAuthorityDoOldGeneration.setGeneration(2);
                federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoOldGeneration, federatedSiteAuthorityDoQueryWrapper);

                //insert new item
                FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
                federatedSiteAuthorityDo.setInstitutions(institutions);
                federatedSiteAuthorityDo.setAuthorityInstitutions(institutionsToCancel);
                federatedSiteAuthorityDo.setCreateTime(date);
                federatedSiteAuthorityDo.setUpdateTime(date);
                federatedSiteAuthorityDo.setStatus(4);
                federatedSiteAuthorityDo.setGeneration(1);
                federatedSiteAuthorityDo.setSequence(maxSequence);
                federatedSiteAuthorityMapper.insert(federatedSiteAuthorityDo);
            } else {
                return false;
            }
        }
        return true;
    }


}
