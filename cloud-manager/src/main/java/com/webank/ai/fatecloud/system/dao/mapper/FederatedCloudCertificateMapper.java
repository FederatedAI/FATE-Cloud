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
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateDo;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateQueryQo;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FederatedCloudCertificateMapper extends BaseMapper<FederatedCloudCertificateDo> {

    @Update("UPDATE t_cloud_certificate_manager SET status = 'Revoked', update_date = NOW() WHERE serial_number = #{serialNumber} AND status = 'Valid'")
    int revoke(String serialNumber);

    @Update("UPDATE t_cloud_certificate_manager SET status = 'Valid',update_date = NOW() WHERE DATE(SUBSTRING(validity,1,10)) < NOW() " +
            "AND DATE(SUBSTRING(validity,12,20)) > NOW() AND status = 'Invalid'")
    void updateCertificateStatusValid();

    @Update("UPDATE t_cloud_certificate_manager SET status = 'Invalid',update_date = NOW() WHERE DATE(SUBSTRING(validity,12,20)) < NOW() AND status = 'Valid'")
    void updateCertificateStatusInvalid();

    int getTotal(CertificateQueryQo certificateQueryQo);

    List<FederatedCloudCertificateDo> selectByPage(CertificateQueryQo certificateQueryQo);
}
