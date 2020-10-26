package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedGroupSetQo;
import com.webank.ai.fatecloud.system.pojo.qo.PageInfoQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface FederatedGroupSetMapper extends BaseMapper<FederatedGroupSetDo> {

    List<FederatedGroupSetDo> selectNewList(@Param("federatedGroupSetQo") FederatedGroupSetQo federatedGroupSetQo, @Param("startIndex") long startIndex);

    List<FederatedGroupSetDo> findPagedRegionInfo(@Param("pageInfoQo") PageInfoQo pageInfoQo, @Param("startIndex") long startIndex);

    FederatedGroupSetDo selectGroupSetDetails(Long groupId);
}
