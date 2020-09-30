package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedFateManagerUserMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteAuthorityMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityApplyStatusDto;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsForFateDto;
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

    public PageBean<InstitutionsForFateDto> findInstitutionsForSite(AuthorityInstitutionsQo authorityInstitutionsQo) {

        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("status", "2").ne("institutions", authorityInstitutionsQo.getInstitutions());
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);
        long institutionsCount = federatedFateManagerUserDos.size();

        PageBean<InstitutionsForFateDto> institutionsForFateDtoPageBean = new PageBean<>(authorityInstitutionsQo.getPageNum(), authorityInstitutionsQo.getPageSize(), institutionsCount);
        long startIndex = institutionsForFateDtoPageBean.getStartIndex();

        //get institutions list
        List<FederatedFateManagerUserDo> pagedInstitutions = federatedFateManagerUserMapper.findPagedInstitutions(authorityInstitutionsQo, startIndex);


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
        for (FederatedFateManagerUserDo federatedFateManagerUserDo : pagedInstitutions) {
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

    public List<FederatedSiteAuthorityDo> findAuthorityApplyDetails(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        return federatedSiteAuthorityMapper.findAuthorityApplyDetails(authorityApplyDetailsQo);
    }


    public List<String> findCurrentAuthority(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyDetailsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        LinkedList<String> institutionsList = new LinkedList<>();
        for (FederatedSiteAuthorityDo federatedSiteAuthorityDo : federatedSiteAuthorityDos) {
            institutionsList.add(federatedSiteAuthorityDo.getAuthorityInstitutions());
        }
        return institutionsList;
    }

    @Transactional
    public void reviewAuthorityApplyDetails(AuthorityApplyReviewQo authorityApplyReviewQo) {
        //change the status to 3(deleted) for old institutions to apply
//        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForOverwriteOldInstitutions = new QueryWrapper<>();
//        federatedSiteAuthorityDoQueryWrapperForOverwriteOldInstitutions.eq("institutions", authorityApplyReviewQo.getInstitutions()).eq("status", 2);
//        FederatedSiteAuthorityDo federatedSiteAuthorityDoForOverwriteOldInstitutions = new FederatedSiteAuthorityDo();
//        federatedSiteAuthorityDoForOverwriteOldInstitutions.setStatus(3);
//        federatedSiteAuthorityMapper.update(federatedSiteAuthorityDoForOverwriteOldInstitutions, federatedSiteAuthorityDoQueryWrapperForOverwriteOldInstitutions);

        //update the status for this new institutions to apply
//        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapperForReview = new QueryWrapper<>();
//        federatedSiteAuthorityDoQueryWrapperForReview.eq("institutions", authorityApplyReviewQo.getInstitutions()).eq("status", 1);
//        FederatedSiteAuthorityDo federatedSiteAuthorityDo = new FederatedSiteAuthorityDo();
//        federatedSiteAuthorityDo.setStatus(authorityApplyReviewQo.getStatus());
//        federatedSiteAuthorityDo.setUpdateTime(new Date());
//        federatedSiteAuthorityMapper.update(federatedSiteAuthorityDo, federatedSiteAuthorityDoQueryWrapperForReview);

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

    public PageBean<AuthorityHistoryDto> findAuthorityHistory(AuthorityHistoryQo authorityHistoryQo) {
        long authorityHistoryCount = federatedSiteAuthorityMapper.findAuthorityHistoryCount();

        PageBean<AuthorityHistoryDto> authorityHistoryDtoPageBean = new PageBean<>(authorityHistoryQo.getPageNum(), authorityHistoryQo.getPageSize(), authorityHistoryCount);
        long startIndex = authorityHistoryDtoPageBean.getStartIndex();

        List<AuthorityHistoryDto> authorityHistoryDtos = federatedSiteAuthorityMapper.findAuthorityHistory(authorityHistoryQo, startIndex);

        authorityHistoryDtoPageBean.setList(authorityHistoryDtos);
        return authorityHistoryDtoPageBean;


    }


    public List<FederatedSiteAuthorityDo> findResultsOfAuthorityApply(AuthorityApplyResultsQo authorityApplyResultsQo) {

        long maxAuthoritySequence = federatedSiteAuthorityMapper.findMaxAuthoritySequence(authorityApplyResultsQo.getInstitutions());

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityApplyResultsQo.getInstitutions()).eq("sequence", maxAuthoritySequence);

        return federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
    }

    public List<String> findAuthorizedInstitutions(AuthorityApplyResultsQo authorityApplyResultsQo) {

        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("authority_institutions", authorityApplyResultsQo.getInstitutions()).eq("status", 2).eq("generation", 1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);

        ArrayList<String> institutionsListWithAuthority = new ArrayList<>();
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

    public PageBean<String> findApprovedInstitutions(AuthorityInstitutionsQo authorityInstitutionsQo) {
        QueryWrapper<FederatedSiteAuthorityDo> federatedSiteAuthorityDoQueryWrapper = new QueryWrapper<>();
        federatedSiteAuthorityDoQueryWrapper.eq("institutions", authorityInstitutionsQo.getInstitutions()).eq("status", "2").eq("generation",1);
        List<FederatedSiteAuthorityDo> federatedSiteAuthorityDos = federatedSiteAuthorityMapper.selectList(federatedSiteAuthorityDoQueryWrapper);
        long institutionsCount = federatedSiteAuthorityDos.size();

        PageBean<String> institutionsForFateDtoPageBean = new PageBean<>(authorityInstitutionsQo.getPageNum(), authorityInstitutionsQo.getPageSize(), institutionsCount);
        long startIndex = institutionsForFateDtoPageBean.getStartIndex();

        //get institutions list
        List<String> approvedInstitutions = federatedSiteAuthorityMapper.findApprovedInstitutions(authorityInstitutionsQo, startIndex);
        institutionsForFateDtoPageBean.setList(approvedInstitutions);

        return institutionsForFateDtoPageBean;

    }
}
