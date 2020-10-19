package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedIpManagerDo;
import com.webank.ai.fatecloud.system.pojo.qo.IpManagerListQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedIpManagerMapper extends BaseMapper<FederatedIpManagerDo> {
    List<FederatedIpManagerDo>  getIpList(@Param("ipManagerListQo") IpManagerListQo ipManagerListQo, @Param("startIndex") long startIndex);
}
