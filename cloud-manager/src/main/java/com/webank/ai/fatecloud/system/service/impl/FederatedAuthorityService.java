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
import com.google.common.collect.Maps;
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

    public List<String> findPendingApply(AuthorityApplyResultsQo authorityApplyResultsQo) {
        ArrayList<String> pendingApplyList = new ArrayList<>();

        QueryWrapper<FederatedSiteAuthorityDo> ew = new QueryWrapper<>();
        ew.select("authority_institutions").eq("institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 1).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(ew);
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            pendingApplyList.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }

        return pendingApplyList;
    }

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
        String institutions = authorityApplyDetailsQo.getInstitutions();
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions");
        federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions));
        federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
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

    public CancelListDto findAuthorizedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo, String scenarioType) {
        CancelListDto cancelListDto = new CancelListDto();
        cancelListDto.setScenarioType(scenarioType);

        if ("3".equals(scenarioType)) {
            //get institutions applied by input institutions
            QueryWrapper<FederatedSiteAuthorityDo> ewForGuest = new QueryWrapper<>();
            ewForGuest.eq("institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> guests = federatedSiteAuthorityMapper.selectList(ewForGuest);
            HashSet<String> guestSet = new HashSet<>();
            for (FederatedSiteAuthorityDo guest : guests) {
                guestSet.add(guest.getAuthorityInstitutions());
            }
            cancelListDto.setGuestList(guestSet);

            //get institution applying the input institutions
            QueryWrapper<FederatedSiteAuthorityDo> ewForHost = new QueryWrapper<>();
            ewForHost.eq("authority_institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> hosts = federatedSiteAuthorityMapper.selectList(ewForHost);
            HashSet<String> hostSet = new HashSet<>();
            for (FederatedSiteAuthorityDo host : hosts) {
                hostSet.add(host.getInstitutions());
            }
            cancelListDto.setHostList(hostSet);

        }

        if ("2".equals(scenarioType)) {
            //get institutions applied by input institutions or applying from input institutions
            QueryWrapper<FederatedSiteAuthorityDo> ewForGuest = new QueryWrapper<>();
            ewForGuest.select("IF(institutions ='" + authorityApplyResultsQo.getInstitutions() + "',authority_institutions,institutions) authority_institutions");
            ewForGuest.and(i -> i.eq("institutions", authorityApplyResultsQo.getInstitutions()).or().eq("authority_institutions", authorityApplyResultsQo.getInstitutions()));
            ewForGuest.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> guests = federatedSiteAuthorityMapper.selectList(ewForGuest);
            HashSet<String> guestSet = new HashSet<>();
            for (FederatedSiteAuthorityDo guest : guests) {
                guestSet.add(guest.getAuthorityInstitutions());
            }
            cancelListDto.setGuestList(guestSet);

        }

        if ("1".equals(scenarioType)) {
            //get institutions applying the input institutions
            QueryWrapper<FederatedSiteAuthorityDo> ewForAll = new QueryWrapper<>();
            ewForAll.select("IF(institutions ='" + authorityApplyResultsQo.getInstitutions() + "',authority_institutions,institutions) authority_institutions");
            ewForAll.and(i -> i.eq("institutions", authorityApplyResultsQo.getInstitutions()).or().eq("authority_institutions", authorityApplyResultsQo.getInstitutions()));
            ewForAll.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> allList = federatedSiteAuthorityMapper.selectList(ewForAll);
            HashSet<String> all = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : allList) {
                all.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
            }
            cancelListDto.setAll(all);
        }

        return cancelListDto;


//        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
//        federatedSiteAuthorityDoQueryWrapper.eq("authority_institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
//        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
//
//        HashSet<String> institutionsListWithAuthority = new HashSet<>();
//        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
//            institutionsListWithAuthority.add(federatedSiteAuthorityDo.getInstitutions());
//        }
//        return institutionsListWithAuthority;

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
            return institutionsForFateDtoPageBean;
        } else {
            institutionsForFateDtoPageBean = new PageBean<>(1, institutionsCount, institutionsCount);
        }

        //get list of institutions which the input institution has accessible authority
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();

        if ("1".equals(scenarioType) || "2".equals(scenarioType)) {
            federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + inputInstitutions + "',authority_institutions,institutions) authority_institutions");
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", inputInstitutions).or().eq("authority_institutions", inputInstitutions))
                    .in("status", 1, 2).eq("generation", 1);
        } else {
            federatedSiteAuthorityDoQueryWrapper.select("authority_institutions");
            federatedSiteAuthorityDoQueryWrapper.eq("institutions", inputInstitutions).in("status", 1, 2).eq("generation", 1);
        }
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

    public PageBean<InstitutionsForFateDto> findApprovedInstitutions(AuthorityInstitutionsQo authorityInstitutionsQo, String scenarioType) {
        String institutions = authorityInstitutionsQo.getInstitutions();
        HashSet<InstitutionsForFateDto> institutionsForFateDtos = new HashSet<>();

        if ("3".equals(scenarioType)) {
            QueryWrapper<FederatedSiteAuthorityDo> ewForApply = new QueryWrapper<>();
            ewForApply.eq("institutions", institutions).ne("status", 1).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(ewForApply);

            Map<String, FederatedSiteAuthorityDo> institutionsForApply = Maps.newHashMap();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
                institutionsForApply.put(federatedSiteAuthorityDo.getAuthorityInstitutions(), federatedSiteAuthorityDo);
            }


            QueryWrapper<FederatedSiteAuthorityDo> ewForApplied = new QueryWrapper<>();
            ewForApplied.eq("authority_institutions", institutions).ne("status", 1).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDosApplied = federatedSiteAuthorityMapper.selectList(ewForApplied);

            Map<String, FederatedSiteAuthorityDo> institutionsForApplied = Maps.newHashMap();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDosApplied) {
                institutionsForApplied.put(federatedSiteAuthorityDo.getInstitutions(), federatedSiteAuthorityDo);
            }

            Set<String> institutionsForApplySet = institutionsForApply.keySet();
            Set<String> institutionsForAppliedSet = institutionsForApplied.keySet();


            //institutions apply
            HashSet<String> resultApply = new HashSet<>();
            resultApply.addAll(institutionsForApplySet);
            resultApply.removeAll(institutionsForAppliedSet);

            for (String institutionsApply : resultApply) {
                FederatedSiteAuthorityDo federatedSiteAuthorityDo = institutionsForApply.get(institutionsApply);

                InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
                institutionsForFateDto.setInstitutions(institutionsApply);
                institutionsForFateDto.setStatus(federatedSiteAuthorityDo.getStatus());
                institutionsForFateDtos.add(institutionsForFateDto);
            }


            //institutions applied
            HashSet<String> resultApplied = new HashSet<>();
            resultApplied.addAll(institutionsForAppliedSet);
            resultApplied.removeAll(institutionsForApplySet);

            for (String institutionsApplied : resultApplied) {
                FederatedSiteAuthorityDo federatedSiteAuthorityDo = institutionsForApplied.get(institutionsApplied);

                InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
                institutionsForFateDto.setInstitutions(institutionsApplied);
                institutionsForFateDto.setStatus(federatedSiteAuthorityDo.getStatus());
                institutionsForFateDtos.add(institutionsForFateDto);
            }

            //institutions both side
            HashSet<String> resultBoth = new HashSet<>();
            resultBoth.addAll(institutionsForAppliedSet);
            resultBoth.retainAll(institutionsForApplySet);

            for (String institutionsBoth : resultBoth) {
                FederatedSiteAuthorityDo federatedSiteAuthorityApply = institutionsForApply.get(institutionsBoth);
                FederatedSiteAuthorityDo federatedSiteAuthorityApplied = institutionsForApplied.get(institutionsBoth);
                Integer statusApply = federatedSiteAuthorityApply.getStatus();
                Integer statusApplied = federatedSiteAuthorityApplied.getStatus();

                InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
                institutionsForFateDto.setInstitutions(institutionsBoth);
                if (statusApply == 2 || statusApplied == 2) {
                    institutionsForFateDto.setStatus(2);
                } else {
                    institutionsForFateDto.setStatus(federatedSiteAuthorityApply.getStatus());

                }
                institutionsForFateDtos.add(institutionsForFateDto);

            }


        } else {
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions", "status");
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions))
                    .ne("status", 1).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
                InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
                institutionsForFateDto.setInstitutions(federatedSiteAuthorityDo.getAuthorityInstitutions());
                institutionsForFateDto.setStatus(federatedSiteAuthorityDo.getStatus());
                institutionsForFateDtos.add(institutionsForFateDto);
            }

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

        //check whether all input institutions are valid or not
        QueryWrapper<FederatedFateManagerUserDo> ewForFateManagerUser = new QueryWrapper<>();
        ewForFateManagerUser.eq("institutions", institutions).eq("status", 2);
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(ewForFateManagerUser);
        if (federatedFateManagerUserDos.size() <= 0) {
            log.error("institution:{} invalid !", institutions);
            return false;
        }
        for (String authorityInstitution : authorityInstitutions) {
            QueryWrapper<FederatedFateManagerUserDo> ewForFateManagerUserToApply = new QueryWrapper<>();
            ewForFateManagerUserToApply.eq("institutions", authorityInstitution).eq("status", 2);
            List<FederatedFateManagerUserDo> federatedFateManagerUserDosToApply = federatedFateManagerUserMapper.selectList(ewForFateManagerUserToApply);
            if (federatedFateManagerUserDosToApply.size() <= 0) {
                log.error("authorityInstitution:{} invalid !", authorityInstitution);
                return false;
            }
        }

        //check whether the input institution has pending applying request or not
        QueryWrapper<FederatedSiteAuthorityDo> ewForApply = new QueryWrapper<>();
        ewForApply.eq("institutions", institutions).eq("status", 1).eq("generation", 1);
        Integer count = federatedSiteAuthorityMapper.selectCount(ewForApply);
        if (count > 0) {
            log.error("institutions:{} has pending apply!", institutions);
            return false;
        }

        //check whether the institutions to apply  has been applied by other institutions or not now
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForExistApply = new QueryWrapper<>();
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
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            if ("1".equals(scenarioType) || "2".equals(scenarioType)) {
                federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", authorityInstitution)).or(k -> k.eq("institutions", authorityInstitution).eq("authority_institutions", institutions)));
                federatedSiteAuthorityDoQueryWrapper.eq("generation", 1);
            } else {
                federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutions).eq("authority_institutions", authorityInstitution).eq("generation", 1);
            }

            FederatedSiteAuthorityDo federatedSiteAuthorityDoToUpdate = new FederatedSiteAuthorityDo();
            federatedSiteAuthorityDoToUpdate.setGeneration(2);
            federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoToUpdate, federatedSiteAuthorityDoQueryWrapper);

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

        if ("1".equals(scenarioType)) {//mix
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions");
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            HashSet<String> all = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
                String authorityInstitutions = federatedSiteAuthorityDo.getAuthorityInstitutions();
                all.add(authorityInstitutions);
            }
            cancelListDto.setAll(all);
        }
        if ("2".equals(scenarioType)) {//guest
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.select("IF(institutions ='" + institutions + "',authority_institutions,institutions) authority_institutions");
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.eq("institutions", institutions).or().eq("authority_institutions", institutions));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            HashSet<String> guestList = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
                String authorityInstitutions = federatedSiteAuthorityDo.getAuthorityInstitutions();
                guestList.add(authorityInstitutions);
            }
            cancelListDto.setGuestList(guestList);
        }
        if ("3".equals(scenarioType)) {//host
            QueryWrapper<FederatedSiteAuthorityDo> ewForHost = new QueryWrapper<>();
            ewForHost.eq("institutions", institutions);
            ewForHost.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDosForHost = federatedSiteAuthorityMapper.selectList(ewForHost);
            HashSet<String> hostList = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDosForHost) {
                String hostInstitutions = federatedSiteAuthorityDo.getAuthorityInstitutions();
                hostList.add(hostInstitutions);
            }
            cancelListDto.setHostList(hostList);

            QueryWrapper<FederatedSiteAuthorityDo> ewForGuest = new QueryWrapper<>();
            ewForGuest.eq("authority_institutions", institutions);
            ewForGuest.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDosForGuest = federatedSiteAuthorityMapper.selectList(ewForGuest);
            HashSet<String> guestList = new HashSet<>();
            for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDosForGuest) {
                String guestInstitutions = federatedSiteAuthorityDo.getInstitutions();
                guestList.add(guestInstitutions);
            }
            cancelListDto.setGuestList(guestList);
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

        if ("1".equals(scenarioType)) {
            HashSet<String> all = cancelListQo.getAll();
            for (String institutionsToCancel : all) {
                QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
                federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsToCancel)).or(k -> k.eq("institutions", institutionsToCancel).eq("authority_institutions", institutions)));
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
        }
        if ("2".equals(scenarioType)) {
            HashSet<String> guestList = cancelListQo.getGuestList();
            for (String institutionsToCancel : guestList) {
                QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
                federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsToCancel)).or(k -> k.eq("institutions", institutionsToCancel).eq("authority_institutions", institutions)));
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
        }

        if ("3".equals(scenarioType)) {
            HashSet<String> guestList = cancelListQo.getGuestList();
            for (String institutionsToCancel : guestList) {
                QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
                federatedSiteAuthorityDoQueryWrapper.eq("institutions", institutionsToCancel).eq("authority_institutions", institutions);
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


            HashSet<String> hostList = cancelListQo.getHostList();
            for (String institutionsToCancel : hostList) {
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
        if ("1".equals(scenarioType)) {//mix type, both see guest an host
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId)).or(k -> k.eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions)));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            return federatedSiteAuthorityDos.size() > 0;

        }
        if ("2".equals(scenarioType)) {// only can see guest
            if (role != 1) {
                return false;
            }
            QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
            federatedSiteAuthorityDoQueryWrapper.and(i -> i.and(j -> j.eq("institutions", institutions).eq("authority_institutions", institutionsOfPartyId)).or(k -> k.eq("institutions", institutionsOfPartyId).eq("authority_institutions", institutions)));
            federatedSiteAuthorityDoQueryWrapper.eq("status", 2).eq("generation", 1);
            List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
            return federatedSiteAuthorityDos.size() > 0;
        }

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
        return false;

    }

    public List<InstitutionsForFateDto> findSelfApprovedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo, String scenarioType) {
        List<InstitutionsForFateDto> institutionsForFateDtos = new LinkedList<>();

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.select("authority_institutions", "status");
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyResultsQo.getInstitutions()).ne("status", 1).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            InstitutionsForFateDto institutionsForFateDto = new InstitutionsForFateDto();
            institutionsForFateDto.setInstitutions(federatedSiteAuthorityDo.getAuthorityInstitutions());
            institutionsForFateDto.setStatus(federatedSiteAuthorityDo.getStatus());
            institutionsForFateDtos.add(institutionsForFateDto);
        }

        return institutionsForFateDtos;
    }
}
