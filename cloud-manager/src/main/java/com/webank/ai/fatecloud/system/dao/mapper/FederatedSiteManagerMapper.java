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
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsDto;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsWithSites;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FederatedSiteManagerMapper extends BaseMapper<FederatedSiteManagerDo> {

    FederatedSiteManagerDo findSiteByPartyId(@Param("partyId") Long partyId,@Param("secretInfo") String secretInfo, @Param("status") Object... status);

    FederatedSiteManagerDo findSite(Long id);

    long findSitesCount(SiteListQo siteListQo);

    List<FederatedSiteManagerDo> findPagedSites(@Param("siteListQo") SiteListQo siteListQo, @Param("startIndex") long startIndex);

    List<FederatedSiteManagerDo> findUsedSites(@Param("usedSiteListQo") UsedSiteListQo usedSiteListQo, @Param("startIndex") long startIndex);

    long findInstitutionsCount();

    List<InstitutionsDto> findInstitutions(@Param("institutionQo") InstitutionQo institutionQo, @Param("startIndex") long startIndex);

    List<FederatedSiteManagerDo> findPagedSitesForFateManager(@Param("siteListForFateManagerQo") SiteListForFateManagerQo siteListForFateManagerQo, @Param("startIndex") long startIndex);

    List<String> findInstitutionsInGroup(InstitutionsInGroup institutionsInGroup);

    long findUsedSitesCount(UsedSiteListQo usedSiteListQo);

    long countForInstitutions(InstitutionQo institutionQo);

    List<InstitutionsWithSites> findInstitutionsWithSites(String institutions);

    long findCountOfSite(JobOfSiteDimensionQo jobOfSiteDimensionQo);

    long findCountOfSitePeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    List<InstitutionsWithSites> findInstitutionsWithSitesPaged(@Param("startIndex") long startIndex, @Param("jobOfSiteDimensionQo") JobOfSiteDimensionQo jobOfSiteDimensionQo);

    List<InstitutionsWithSites> findInstitutionsWithSitesPagedPeriod(@Param("startIndex") long startIndex, @Param("jobOfSiteDimensionPeriodQo") JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo);

    long findSitesCountForIp(IpManagerListQo ipManagerListQo);

    List<FederatedSiteManagerDo> findPagedSitesForIp(@Param("ipManagerListQo") IpManagerListQo ipManagerListQo, @Param("startIndex") long startIndex);

    List<Integer> getInstitutionsType(String institutions);

    long selectCountByScenario(String institutions, String type);

    List<FederatedSiteManagerDo> findSitesByScenario(@Param("siteListForFateManagerQo") SiteListForFateManagerQo siteListForFateManagerQo,@Param("startIndex") long startIndex,@Param("type") String type);
}

