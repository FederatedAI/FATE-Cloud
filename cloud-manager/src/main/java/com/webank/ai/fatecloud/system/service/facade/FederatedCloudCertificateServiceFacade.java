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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.CharacterUtil;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateFileQo;
import com.webank.ai.fatecloud.system.pojo.qo.CertificateQueryQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedCloudCertificateService;
import com.webank.ai.fatecloud.system.service.impl.FederatedCloudCertificateTypeService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FederatedCloudCertificateServiceFacade {
    @Autowired
    private FederatedCloudCertificateService federatedCloudCertificateService;

    @Autowired
    private FederatedCloudCertificateTypeService federatedCloudCertificateTypeService;

    @Resource
    private RestTemplate restTemplate;

    @Value("${chain.ca.url}")
    private String caUrl;
    @Value("${chain.ca.genera.url}")
    private String genera;
    @Value("${chain.ca.revoke.url}")
    private String revoke;
    @Value("${chain.ca.update.url}")
    private String update;
    @Value("${chain.ca.file.url}")
    private String fileDownload;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /***
     * save certificate info
     */
    public CommonResponse<String> save(FederatedCloudCertificateDo certificateDo) {
        CommonResponse<String> result = paramCheck(certificateDo, true);

        if (result == null) {
            FederatedCloudCertificateTypeDo typeDo = federatedCloudCertificateTypeService.queryById(certificateDo.getTypeId());

            if (typeDo == null || StringUtils.isEmpty(typeDo.getTypeName())) {
                return new CommonResponse<>(400, "Unknown certificate type");
            }

            String siteAuthority = certificateDo.getSiteAuthority();
            String institution = certificateDo.getInstitution();
            String organization = certificateDo.getOrganization();
            try {
                certificateDo.setDnsName(getDnsName(siteAuthority, typeDo.getTypeName(), institution, organization));
            } catch (Exception e) {
                log.error("Domain generated error", e);
                return new CommonResponse<>(400, "Site authority, institution, organization. Illegal characters cannot be used");
            }
            if (federatedCloudCertificateService.save(certificateDo)) {
                return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
            }
            result = new CommonResponse<>(ReturnCodeEnum.CERTIFICATE_UPDATE_ERROR);
        }
        return result;
    }

    /***
     * update certificate info
     */
    @Transactional
    public CommonResponse<String> update(FederatedCloudCertificateDo certificateDo) {
        CommonResponse<String> resultRes = paramCheck(certificateDo, StringUtils.isEmpty(certificateDo.getSerialNumber()));
        if (resultRes != null) {
            return resultRes;
        }

        FederatedCloudCertificateTypeDo typeDo = federatedCloudCertificateTypeService.queryById(certificateDo.getTypeId());
        if (typeDo == null || StringUtils.isEmpty(typeDo.getTypeName())) {
            return new CommonResponse<>(400, "Unknown certificate type");
        }

        String siteAuthority = certificateDo.getSiteAuthority();
        if (StringUtils.isEmpty(certificateDo.getSerialNumber())) {
            String institution = certificateDo.getInstitution();
            String organization = certificateDo.getOrganization();
            try {
                certificateDo.setDnsName(getDnsName(siteAuthority, typeDo.getTypeName(), institution, organization));
            } catch (Exception e) {
                log.error("Domain generated error", e);
                return new CommonResponse<>(400, "Site authority, institution, organization. Illegal characters cannot be used");
            }
        }

        if (federatedCloudCertificateService.update(certificateDo)) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.CERTIFICATE_UPDATE_ERROR);
    }

    /***
     * publish certificate
     */
    @Transactional
    public CommonResponse<String> publish(FederatedCloudCertificateDo certificateQueryQo) {
        Preconditions.checkArgument(certificateQueryQo.getId() != null);
        List<FederatedCloudCertificateDo> query = federatedCloudCertificateService.query(certificateQueryQo);

        if (query != null && query.size() == 1) {

            FederatedCloudCertificateDo cloudCertificateDo = query.get(0);
            boolean flag;
            try {
                String[] valid = cloudCertificateDo.getValidity().split("~");
                Date date = new Date();
                flag = sdf.parse(valid[0] = valid[0] + " 00:00:00").before(date);
                if (sdf.parse(valid[1] = valid[1] + " 23:59:59").before(date))
                    return new CommonResponse<>(400, "The certificate expiration time cannot be less than the current time");
                cloudCertificateDo.setValidity(valid[0] + "~" + valid[1]);
            } catch (Exception e) {
                return new CommonResponse<>(500, "Invalid certificate effective time");
            }

            if ("Unpublished".equals(cloudCertificateDo.getStatus())) {

                FederatedCloudCertificateTypeDo typeDo = federatedCloudCertificateTypeService.queryById(cloudCertificateDo.getTypeId());
                if (typeDo != null && typeDo.getTypeName() != null) {

                    Map<String, Object> requestBody = new LinkedHashMap<>();
                    requestBody.put("productType", typeDo.getTypeName());
                    requestBody.put("validity", cloudCertificateDo.getValidity());
                    requestBody.put("institution", cloudCertificateDo.getInstitution());
                    requestBody.put("siteAuthority", cloudCertificateDo.getSiteAuthority());
                    requestBody.put("notes", cloudCertificateDo.getNotes());
                    requestBody.put("dnsName", cloudCertificateDo.getDnsName());
                    Map<String, String> subjName = new LinkedHashMap<>();
                    subjName.put("orgName", cloudCertificateDo.getOrganization());
                    subjName.put("orgUnitName", cloudCertificateDo.getInstitution());
                    subjName.put("commonName", cloudCertificateDo.getDnsName());
                    requestBody.put("subjName", subjName);

                    // invoke ca genera certificate interface
                    CommonResponse result = restTemplate.postForObject(caUrl + genera, requestBody, CommonResponse.class);
                    if (result != null && result.getCode() == 0 && result.getData() != null) {
                        String serialNumber = result.getData().toString();
                        FederatedCloudCertificateDo certificateDo = new FederatedCloudCertificateDo();
                        certificateDo.setId(certificateQueryQo.getId());
                        certificateDo.setStatus(flag ? "Valid" : "Invalid");
                        certificateDo.setSerialNumber(serialNumber);

                        if (federatedCloudCertificateService.update(certificateDo)) {
                            return new CommonResponse<>(ReturnCodeEnum.SUCCESS, serialNumber);
                        } else {
                            log.warn("an inconsistent certificate has appeared, certificate serial number: {}", serialNumber);
                        }
                    } else if (result != null && StringUtils.isNotBlank(result.getMsg())) {
                        return new CommonResponse<>(400, result.getMsg());
                    }
                }
            }
        }
        return new CommonResponse<>(ReturnCodeEnum.CERTIFICATE_UPDATE_ERROR);
    }

    /***
     * revoke certificate
     */
    @Transactional
    public CommonResponse<String> revoke(String serialNumber) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(serialNumber));
        FederatedCloudCertificateDo certificateDo = new FederatedCloudCertificateDo();
        certificateDo.setSerialNumber(serialNumber);

        if (federatedCloudCertificateService.invalidate(serialNumber)) {
            // invoke ca revoke certificate interface
            Map<String, String> body = new LinkedHashMap<>();
            body.put("serialNumber", serialNumber);
            CommonResponse result = restTemplate.postForObject(caUrl + revoke, body, CommonResponse.class);
            if (result != null && result.getCode() == 0) {
                return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
            } else {
                certificateDo.setStatus("Valid");
                if (!federatedCloudCertificateService.updateBySerialNumber(certificateDo)) {
                    throw new RuntimeException("rollback");
                }
                if (result != null && StringUtils.isNotBlank(result.getMsg())) {
                    return new CommonResponse<>(400, result.getMsg());
                }
            }
        }

        return new CommonResponse<>(ReturnCodeEnum.CERTIFICATE_UPDATE_ERROR);
    }

    /***
     * get certificate file
     */
    public CertificateFileQo getFile(FederatedCloudCertificateDo certInfo) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(certInfo.getSerialNumber()));

        List<FederatedCloudCertificateDo> query = federatedCloudCertificateService.query(certInfo);
        Preconditions.checkArgument(query != null && query.size() == 1 && !"Revoked".equals(query.get(0).getStatus()),
                "download failure, serial number: " + certInfo.getSerialNumber());

        // invoke ca revoke certificate interface
        CommonResponse result = restTemplate.postForObject(caUrl + fileDownload, certInfo, CommonResponse.class);
        if (result == null || result.getCode() != 0 || result.getData() == null) {
            log.error("download request failed, certInfo: {}", certInfo.getSerialNumber());
            throw new IllegalArgumentException();
        }
        return JSONObject.parseObject(JSONObject.toJSONString(result.getData()), CertificateFileQo.class);
    }

    /***
     * query certificate info
     */
    public CommonResponse<IPage<FederatedCloudCertificateDo>> query(CertificateQueryQo certificateQueryQo) {
        IPage<FederatedCloudCertificateDo> certificateDoIPage = federatedCloudCertificateService.queryByPage(certificateQueryQo);
        if (certificateDoIPage == null || certificateDoIPage.getSize() < 1) {
            return new CommonResponse<>(ReturnCodeEnum.SECRET_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, certificateDoIPage);
    }

    private CommonResponse<String> paramCheck(FederatedCloudCertificateDo certificateDo, boolean checkValidity) {
        boolean noneEmpty = StringUtils.isNoneEmpty(
                certificateDo.getInstitution(),
                certificateDo.getOrganization()) && certificateDo.getTypeId() != null;

        if (noneEmpty) {
            if (checkValidity) {
                try {
                    String[] validity = certificateDo.getValidity().split("~");

                    Date current = new Date();
                    Date endDate = sdf.parse(validity[1] + " 23:59:59");
                    if (validity.length != 2 || sdf.parse(validity[0] + " 00:00:00").after(endDate) || current.after(endDate)) {
                        return new CommonResponse<>(400, "Validity not in a reasonable time period");
                    }
                } catch (Exception p) {
                    return new CommonResponse<>(400, "Validity wrong format, eg: 2021-03-05~2025-03-09");
                }
            }
        } else {
            return new CommonResponse<>(400, "Required parameters cannot be empty");
        }
        return null;
    }

    private String getDnsName(String site, String typeName, String institution, String organization) throws BadHanyuPinyinOutputFormatCombination {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(site)) {
            sb.append('*');
        } else {
            if ((site = site.trim()).split(",").length != 1) {
                sb.append('*');
            } else {
                sb.append(site.replace(",", "")).append('.');
            }
        }

        String tn = CharacterUtil.specialCharacters2Pinyin(typeName, '-');
        String ins = CharacterUtil.specialCharacters2Pinyin(institution, '-');
        String org = CharacterUtil.specialCharacters2Pinyin(organization, '-');

        return sb.append(tn).append('.').append(ins).append(".virtual.").append(org).append(".com").toString();
    }
}
