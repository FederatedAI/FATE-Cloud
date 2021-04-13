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
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FederatedCloudCertificateTypeMapper extends BaseMapper<FederatedCloudCertificateTypeDo> {
    @Select("SELECT id,type_name AS typeName,(SELECT CASE WHEN COUNT(type_id) > 0 THEN 2 ELSE 3 END FROM t_cloud_certificate_manager WHERE type_id = ct.id) AS canDelete FROM t_cloud_certificate_type ct")
    List<FederatedCloudCertificateTypeDo> queryAll();

    @Insert("INSERT INTO t_cloud_certificate_type (type_name) SELECT #{typeName} FROM DUAL WHERE NOT EXISTS (SELECT type_name FROM t_cloud_certificate_type WHERE type_name = #{typeName})")
    int initDefaultType(FederatedCloudCertificateTypeDo typeDo);

    @Select("SELECT COUNT(1) FROM t_cloud_certificate_type")
    int count();
}
