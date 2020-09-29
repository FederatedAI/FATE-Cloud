package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsDto;
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

}

