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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudCertificateMapper;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateQueryQo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FederatedCloudCertificateService {
    @Autowired
    private FederatedCloudCertificateMapper federatedCloudCertificateMapper;

    public boolean save(FederatedCloudCertificateDo federatedCloudCertificateDo) {
        Date newDate = new Date();
        federatedCloudCertificateDo.setCreateDate(newDate);
        federatedCloudCertificateDo.setUpdateDate(newDate);
        log.info("create certificate, cert info: {}", federatedCloudCertificateDo);
        return federatedCloudCertificateMapper.insert(federatedCloudCertificateDo) > 0;
    }

    public boolean update(FederatedCloudCertificateDo federatedCloudCertificateDo) {
        federatedCloudCertificateDo.setUpdateDate(new Date());
        log.info("update certificate, cert info: {}", federatedCloudCertificateDo);
        return federatedCloudCertificateMapper.updateById(federatedCloudCertificateDo) > 0;
    }

    public boolean invalidate(String serialNumber) {
        log.info("revoke certificate, cert serial number: {}", serialNumber);
        return federatedCloudCertificateMapper.revoke(serialNumber) > 0;
    }

    public IPage<FederatedCloudCertificateDo> queryByPage(CertificateQueryQo certificateQueryQo) {
        int pageNum = certificateQueryQo.getPageNum() < 1 ? 1 : certificateQueryQo.getPageNum();
        Integer pageSize = certificateQueryQo.getPageSize();
        Page<FederatedCloudCertificateDo> page = new Page<>(pageNum, pageSize);
        certificateQueryQo.setPageNum((pageNum - 1) * pageSize);

        if (StringUtils.isNotEmpty(certificateQueryQo.getQueryString())) {
            certificateQueryQo.setQueryString("%" + certificateQueryQo.getQueryString() + "%");
        }

        List<FederatedCloudCertificateDo> result = federatedCloudCertificateMapper.selectByPage(certificateQueryQo);
        page.setTotal(federatedCloudCertificateMapper.getTotal(certificateQueryQo)).setRecords(result);
        return page;
    }

    public List<FederatedCloudCertificateDo> query(FederatedCloudCertificateDo federatedCloudCertificateDo) {
        return federatedCloudCertificateMapper.selectList(new QueryWrapper<>(federatedCloudCertificateDo));
    }

    public boolean updateBySerialNumber(FederatedCloudCertificateDo certificateDo) {
        certificateDo.setUpdateDate(new Date());
        log.info("rollback update certificate info, cert info: {}", certificateDo);
        return federatedCloudCertificateMapper.update(certificateDo,
                new UpdateWrapper<>(((FederatedCloudCertificateDo) null)).eq("serial_number", certificateDo.getSerialNumber())) > 0;
    }
}
