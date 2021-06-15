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
package com.webank.ai.fatecloud.system.service.facade;

import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.config.InitializeCertificateConfig;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import com.webank.ai.fatecloud.system.service.impl.FederatedCloudCertificateService;
import com.webank.ai.fatecloud.system.service.impl.FederatedCloudCertificateTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class FederatedCloudCertificateTypeServiceFacade {
    @Value("${certificate.type.count}")
    private Integer typeCount;

    @Autowired
    private FederatedCloudCertificateTypeService federatedCloudCertificateTypeService;

    @Autowired
    private FederatedCloudCertificateService federatedCloudCertificateService;

    public CommonResponse<List<FederatedCloudCertificateTypeDo>> queryAll() {
        List<FederatedCloudCertificateTypeDo> result = federatedCloudCertificateTypeService.queryAll();
        for (FederatedCloudCertificateTypeDo typeDo : result) {
            if (InitializeCertificateConfig.defaultType.contains(typeDo.getTypeName())) {
                typeDo.setCanDelete(1);
            }
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, result);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CommonResponse<String> update(List<FederatedCloudCertificateTypeDo> certificateTypeDoList) {

        for (FederatedCloudCertificateTypeDo certificateTypeDo : certificateTypeDoList) {
            boolean success;
            if (certificateTypeDo.getId() == null) {
                success = federatedCloudCertificateTypeService.getCount() < typeCount;
                success = success && federatedCloudCertificateTypeService.save(certificateTypeDo);
                Preconditions.checkArgument(success, "save type error, error type name: " + certificateTypeDo.getTypeName());
                log.info("save certificate type, info: {}", certificateTypeDo);
                continue;
            }
            if (StringUtils.isEmpty(certificateTypeDo.getTypeName())) {
                FederatedCloudCertificateDo cloudCertificateDo = new FederatedCloudCertificateDo();
                cloudCertificateDo.setTypeId(certificateTypeDo.getId());
                List<FederatedCloudCertificateDo> certificateDos = federatedCloudCertificateService.query(cloudCertificateDo);
                if (certificateDos == null || certificateDos.isEmpty()) {
                    success = federatedCloudCertificateTypeService.delete(certificateTypeDo);
                    Preconditions.checkArgument(success, "delete type error, error type id: " + certificateTypeDo.getId());
                }
                log.info("delete certificate type, info: {}", certificateTypeDo);
                continue;
            }
            success = federatedCloudCertificateTypeService.update(certificateTypeDo);
            Preconditions.checkArgument(success, "update type error, error type id: " + certificateTypeDo.getId());
            log.info("update certificate type, info: {}", certificateTypeDo);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }
}
