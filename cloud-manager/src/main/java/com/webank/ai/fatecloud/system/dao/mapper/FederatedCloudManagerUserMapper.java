package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserPagedQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedCloudManagerUserMapper extends BaseMapper<FederatedCloudManagerUserDo> {

    List<FederatedCloudManagerUserDo> findPagedCloudUser(@Param("cloudManagerUserPagedQo") CloudManagerUserPagedQo cloudManagerUserPagedQo, @Param("startIndex") long startIndex);

}
