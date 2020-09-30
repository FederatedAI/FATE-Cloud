package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityApplyStatusDto;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedSiteAuthorityMapper extends BaseMapper<FederatedSiteAuthorityDo> {
    Long findMaxSequence();

    List<AuthorityApplyStatusDto> findAuthorityApplyStatus(AuthorityApplyStatusQo authorityApplyStatusQo);

    List<FederatedSiteAuthorityDo> findAuthorityApplyDetails(AuthorityApplyDetailsQo authorityApplyDetailsQo);

    long findAuthorityHistoryCount();

    List<AuthorityHistoryDto> findAuthorityHistory(@Param("authorityHistoryQo") AuthorityHistoryQo authorityHistoryQo, @Param("startIndex") long startIndex);

    long findMaxAuthoritySequence(String institutions);

    long findAuthorityHistoryOfFateManagerCount(AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo);

    List<AuthorityHistoryDto> findAuthorityHistoryOfFateManager(@Param("authorityHistoryOfFateManagerQo") AuthorityHistoryOfFateManagerQo authorityHistoryOfFateManagerQo, @Param("startIndex") long startIndex);

    List<String> findApprovedInstitutions(@Param("authorityInstitutionsQo") AuthorityInstitutionsQo authorityInstitutionsQo, @Param("startIndex") long startIndex);
}
