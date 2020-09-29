package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.pojo.qo.ModelHistoryQo;
import com.webank.ai.fatecloud.system.pojo.qo.SiteListWithModelsQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedModelMapper extends BaseMapper<FederatedSiteModelDo> {
    int selectCount(SiteListWithModelsQo siteListWithModelsQo);

    List<FederatedSiteManagerDo> findPage(@Param(value = "siteListWithModelsQo") SiteListWithModelsQo siteListWithModelsQo, @Param(value = "startIndex") long startIndex);

}
