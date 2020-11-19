package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionPageQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedProductVersionMapper extends BaseMapper<FederatedProductVersionDo> {

    long count(ProductVersionPageQo productVersionPageQo);

    List<FederatedProductVersionDo> page(@Param(value = "startIndex") long startIndex, @Param(value = "productVersionPageQo") ProductVersionPageQo productVersionPageQo);

    List<String> getProductNames();

    List<String> getProductVersions();
}
