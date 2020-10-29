package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedExchangeMapper extends BaseMapper<FederatedExchangeDo> {
    List<FederatedExchangeDo> findExchangePage(@Param("startIndex") long startIndex, @Param("exchangePageQo") ExchangePageQo exchangePageQo);
}
