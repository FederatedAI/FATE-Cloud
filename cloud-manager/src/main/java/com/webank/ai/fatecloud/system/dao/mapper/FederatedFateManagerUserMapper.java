package com.webank.ai.fatecloud.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsDto;
import com.webank.ai.fatecloud.system.pojo.qo.AuthorityInstitutionsQo;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserPagedQo;
import com.webank.ai.fatecloud.system.pojo.qo.InstitutionQo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FederatedFateManagerUserMapper extends BaseMapper<FederatedFateManagerUserDo> {

    List<FederatedFateManagerUserDo> findPagedInstitutions(@Param("authorityInstitutionsQo") AuthorityInstitutionsQo authorityInstitutionsQo, @Param("startIndex") long startIndex);

    List<FederatedFateManagerUserDo> findPagedFateManagerUser(@Param("fateManagerUserPagedQo") FateManagerUserPagedQo fateManagerUserPagedQo, @Param("startIndex") long startIndex);

}
