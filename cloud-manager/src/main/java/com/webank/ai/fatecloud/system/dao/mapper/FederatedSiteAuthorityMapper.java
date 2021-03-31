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
package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteAuthorityDo;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityApplyStatusDto;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsForFateDto;
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

    List<InstitutionsForFateDto> findApprovedInstitutions(@Param("authorityInstitutionsQo") AuthorityInstitutionsQo authorityInstitutionsQo, @Param("startIndex") long startIndex);

    FederatedSiteAuthorityDo checkPartyIdAuthority(PartyIdCheckQo partyIdCheckQo);
}
