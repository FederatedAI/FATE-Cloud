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
package com.webank.ai.fatecloud.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateDo;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateFileQo;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateQueryQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedCloudCertificateServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping(value = "/api/certificate")
@Api(tags = "FederatedCertificateController", description = "certificate manage")
public class FederatedCertificateController {
    @Autowired
    private FederatedCloudCertificateServiceFacade federatedCloudCertificateServiceFacade;
    private final BASE64Decoder base64Decoder = new BASE64Decoder();

    @ResponseBody
    @PostMapping(value = "/save")
    @ApiOperation(value = "save certificate info")
    public CommonResponse<String> save(@RequestBody FederatedCloudCertificateDo federatedCloudCertificateDo) {
        return federatedCloudCertificateServiceFacade.save(federatedCloudCertificateDo);
    }

    @ResponseBody
    @PostMapping(value = "/update")
    @ApiOperation(value = "update certificate info")
    public CommonResponse<String> update(@RequestBody FederatedCloudCertificateDo federatedCloudCertificateDo) {
        return federatedCloudCertificateServiceFacade.update(federatedCloudCertificateDo);
    }

    @ResponseBody
    @PostMapping(value = "/publish")
    @ApiOperation(value = "publish certificate")
    public CommonResponse<String> publish(@RequestBody FederatedCloudCertificateDo certificateQueryQo) {
        return federatedCloudCertificateServiceFacade.publish(certificateQueryQo);
    }

    @ResponseBody
    @PostMapping(value = "/revoke")
    @ApiOperation(value = "revoke certificate")
    public CommonResponse<String> invalidate(@RequestBody CertificateQueryQo certInfo) {
        return federatedCloudCertificateServiceFacade.revoke(certInfo.getSerialNumber());
    }

    @ResponseBody
    @PostMapping(value = "/query")
    @ApiOperation(value = "query certificate info")
    public CommonResponse<IPage<FederatedCloudCertificateDo>> query(@RequestBody CertificateQueryQo certificateQueryQo) {
        return federatedCloudCertificateServiceFacade.query(certificateQueryQo);
    }

    @PostMapping(value = "/download")
    @ApiOperation(value = "download certificate tar.gz file")
    public void download(HttpServletResponse response, @RequestBody FederatedCloudCertificateDo certInfo) {
        CertificateFileQo certificateFile = federatedCloudCertificateServiceFacade.getFile(certInfo);

        response.setContentType("text/html; charset=UTF-8");
        String fileName = certInfo.getSerialNumber() + "_" + certificateFile.getFileName();
        fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
        String fileBody = certificateFile.getFileBody();

        try (OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = base64Decoder.decodeBuffer(fileBody);

            // set headers
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            response.setHeader("content-length", buffer.length + "");

            os.write(buffer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
