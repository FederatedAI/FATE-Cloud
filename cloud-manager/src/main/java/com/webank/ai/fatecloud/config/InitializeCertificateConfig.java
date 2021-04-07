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
package com.webank.ai.fatecloud.config;

import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudCertificateMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudCertificateTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Configuration
public class InitializeCertificateConfig implements ApplicationRunner {
    public static final List<String> defaultType = Arrays.asList("general", "FATE", "FATE Serving");

    @Autowired
    private FederatedCloudCertificateMapper federatedCloudCertificateMapper;

    @Autowired
    private FederatedCloudCertificateTypeMapper federatedCloudCertificateTypeMapper;

    @Override
    public void run(ApplicationArguments args) {
        FederatedCloudCertificateTypeDo typeDo = new FederatedCloudCertificateTypeDo();
        for (String type : defaultType) {
            typeDo.setTypeName(type);
            federatedCloudCertificateTypeMapper.initDefaultType(typeDo);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void updateCertificateStatus() {
        federatedCloudCertificateMapper.updateCertificateStatusValid();
        federatedCloudCertificateMapper.updateCertificateStatusInvalid();
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(30000);
        requestFactory.setConnectTimeout(2000);
        return new RestTemplate(requestFactory);
    }
}
