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
package com.webank.ai.fatecloud.system.service.impl;

import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudCertificateTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FederatedCloudCertificateTypeService {
    @Autowired
    private FederatedCloudCertificateTypeMapper federatedCloudCertificateTypeMapper;

    public boolean save(FederatedCloudCertificateTypeDo typeDo) {
        return federatedCloudCertificateTypeMapper.initDefaultType(typeDo) > 0;
    }

    public boolean delete(FederatedCloudCertificateTypeDo typeDo) {
        return federatedCloudCertificateTypeMapper.deleteById(typeDo.getId()) > 0;
    }

    public List<FederatedCloudCertificateTypeDo> queryAll() {
        return federatedCloudCertificateTypeMapper.queryAll();
    }

    public boolean update(FederatedCloudCertificateTypeDo typeDo) {
        return federatedCloudCertificateTypeMapper.updateById(typeDo) > 0;
    }

    public FederatedCloudCertificateTypeDo queryById(Long id) {
        return federatedCloudCertificateTypeMapper.selectById(id);
    }

	public int getCount() {
		return federatedCloudCertificateTypeMapper.count() ;
    }
}
