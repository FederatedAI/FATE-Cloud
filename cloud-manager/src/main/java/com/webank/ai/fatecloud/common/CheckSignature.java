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
package com.webank.ai.fatecloud.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.service.impl.FederatedFateManagerUserService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class CheckSignature {
    @Autowired
    FederatedSiteManagerService federatedSiteManagerService;

    @Autowired
    FederatedFateManagerUserService federatedFateManagerUserService;


    @Value(value = "${checksignature}")
    Boolean checksignature;

    public boolean checkSignature(HttpServletRequest httpServletRequest, String httpBody, Object... status) {
        if (checksignature) {
            String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
            String partyId = httpServletRequest.getHeader(Dict.PARTY_ID);
            String role = httpServletRequest.getHeader(Dict.ROLE);
            String appKey = httpServletRequest.getHeader(Dict.APP_KEY);
            String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
            String nonce = httpServletRequest.getHeader(Dict.NONCE);
            String httpURI = httpServletRequest.getRequestURI();
            log.info("Head Information | partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}", partyId, role, appKey, timestamp, nonce, httpURI, httpBody, signature);
            Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, partyId, role, appKey, timestamp, nonce, httpURI));
            if (status == null || 0 == status.length) {
                throw new IllegalArgumentException();
            }

            SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(partyId), appKey, status);

            if (site == null) {
                return false;
            }
            SecretInfo secretInfo = site.getSecretInfo();
            String key = secretInfo.getKey();
            String secret = secretInfo.getSecret();

            String headSignature = EncryptUtil.generateSignature(secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);
            String trueSignature = EncryptUtil.generateSignature(secret, partyId, Integer.toString(site.getRole()), key, timestamp, nonce, httpURI, httpBody);

            log.info("Request Signature: {}", signature);
            log.info("Head Signature: {} | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", headSignature, secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);
            log.info("True Signature: {} | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", trueSignature, secret, partyId, site.getRole(), key, timestamp, nonce, httpURI, httpBody);

            return StringUtils.equals(signature, trueSignature) && StringUtils.equals(headSignature, trueSignature);
        }

        return true;
    }

    public boolean checkSignatureNew(HttpServletRequest httpServletRequest, String httpBody, String type, int[] fateManagerUserStatus, Object... siteStatus) {
        //check whether need to verify the signature
        if (checksignature) {

            //judge the type of request
            if (Dict.FATE_MANAGER_USER.equals(type)) {

                //check header parameters
                String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
                String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
                String fateManagerUserAppKey = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_APP_KEY);
                String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
                String nonce = httpServletRequest.getHeader(Dict.NONCE);
                String httpURI = httpServletRequest.getRequestURI();
                Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, fateManagerUserId, fateManagerUserAppKey, timestamp, nonce, httpURI));
                log.info(
                        "Head Information | fateManagerUserId:{},fateManagerUserAppKey:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}",
                        fateManagerUserId, fateManagerUserAppKey, timestamp, nonce, httpURI, httpBody, signature
                );

                if (fateManagerUserStatus == null || 0 == fateManagerUserStatus.length) {
                    throw new IllegalArgumentException();
                }

                //get data of the fate manager user
                FederatedFateManagerUserDo fateManagerUser = federatedFateManagerUserService.findFateManagerUser(fateManagerUserId);
                if (fateManagerUser == null) {
                    return false;
                }

                //check fate manager user status
                boolean fateManagerUserStatusResult = false;
                for (int i : fateManagerUserStatus) {
                    if (i == fateManagerUser.getStatus()) {
                        fateManagerUserStatusResult = true;
                        break;
                    }
                }
                if (!fateManagerUserStatusResult) {
                    return false;
                }

                String fateManagerSecretInfoString = fateManagerUser.getSecretInfo();
                SecretInfo fateManagerSecretInfo = JSON.parseObject(fateManagerSecretInfoString, new TypeReference<SecretInfo>() {
                });
                String fateManagerUserKey = fateManagerSecretInfo.getKey();
                String fateManagerUserSecret = fateManagerSecretInfo.getSecret();

                if (!fateManagerUserAppKey.equals(fateManagerUserKey)) {
                    return false;
                }

                String trueSignature = EncryptUtil.generateSignature(fateManagerUserSecret, fateManagerUserId, fateManagerUserKey, timestamp, nonce, httpURI, httpBody);

                log.info(
                        "True Information | fateManagerUserId:{},fateManagerUserAppKey:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{},fateManagerUserSecret:{}",
                        fateManagerUserId, fateManagerUserAppKey, timestamp, nonce, httpURI, httpBody, trueSignature, fateManagerUserSecret
                );


                return StringUtils.equals(signature, trueSignature);

            } else {
                if (Dict.FATE_SITE_USER.equals(type)) {
                    //check header parameters
                    String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
                    String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
                    String fateManagerUserAppKey = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_APP_KEY);
                    String partyId = httpServletRequest.getHeader(Dict.PARTY_ID);
                    String role = httpServletRequest.getHeader(Dict.ROLE);
                    String appKey = httpServletRequest.getHeader(Dict.APP_KEY);
                    String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
                    String nonce = httpServletRequest.getHeader(Dict.NONCE);
                    String httpURI = httpServletRequest.getRequestURI();

                    log.info(
                            "Head Information | fateManagerUserId:{},fateManagerUserAppKey:{},partyId:{},role:{},appKey:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}",
                            fateManagerUserId, fateManagerUserAppKey, partyId, role, appKey, timestamp, nonce, httpURI, httpBody, signature
                    );
                    Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, fateManagerUserId, fateManagerUserAppKey, partyId, role, appKey, timestamp, nonce, httpURI));

                    if (fateManagerUserStatus == null || 0 == fateManagerUserStatus.length) {
                        throw new IllegalArgumentException();
                    }

                    if (siteStatus == null || 0 == siteStatus.length) {
                        throw new IllegalArgumentException();
                    }

                    //get data of the fate manager user
                    FederatedFateManagerUserDo fateManagerUser = federatedFateManagerUserService.findFateManagerUser(fateManagerUserId);
                    if (fateManagerUser == null) {
                        return false;
                    }

                    //check fate manager user status
                    boolean fateManagerUserStatusResult = false;
                    for (int i : fateManagerUserStatus) {
                        if (i == fateManagerUser.getStatus()) {
                            fateManagerUserStatusResult = true;
                            break;
                        }
                    }
                    if (!fateManagerUserStatusResult) {
                        return false;
                    }

                    String fateManagerSecretInfoString = fateManagerUser.getSecretInfo();
                    SecretInfo fateManagerSecretInfo = JSON.parseObject(fateManagerSecretInfoString, new TypeReference<SecretInfo>() {
                    });
                    String fateManagerUserKey = fateManagerSecretInfo.getKey();
                    String fateManagerUserSecret = fateManagerSecretInfo.getSecret();

                    if (!fateManagerUserAppKey.equals(fateManagerUserKey)) {
                        return false;
                    }


                    //get site information
                    SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(partyId), appKey, siteStatus);

                    if (site == null) {
                        return false;
                    }

                    //check site belong to institutions
                    if (!fateManagerUser.getInstitutions().equals(site.getInstitutions())) {
                        return false;
                    }


                    SecretInfo siteSecretInfo = site.getSecretInfo();
                    String siteKey = siteSecretInfo.getKey();
                    String siteSecret = siteSecretInfo.getSecret();

                    if (!appKey.equals(siteKey)) {
                        return false;
                    }

                    String trueSignature = EncryptUtil.generateSignature(fateManagerUserSecret + siteSecret,
                            fateManagerUserId, fateManagerUserAppKey, partyId, Integer.toString(site.getRole()), appKey, timestamp, nonce, httpURI, httpBody);

                    log.info(
                            "True Information | fateManagerUserId:{},fateManagerUserAppKey:{},partyId:{},role:{},appKey:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{},fateManagerUserSecret:{},siteSecret:{}",
                            fateManagerUserId, fateManagerUserAppKey, partyId, site.getRole(), appKey, timestamp, nonce, httpURI, httpBody, trueSignature, fateManagerUserSecret, siteSecret
                    );

                    return StringUtils.equals(signature, trueSignature);
                } else {
                    return false;
                }
            }

        }
        return true;
    }

    // v4 short link activate
    public boolean shortLinkActivateCheckSignature(HttpServletRequest httpServletRequest, String httpBody, String type, int[] fateManagerUserStatus, Object... siteStatus) {
        // judge the type of request
        if (Dict.FATE_MANAGER_USER.equals(type)) {

            // check header parameters
            String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
            String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
            String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
            String nonce = httpServletRequest.getHeader(Dict.NONCE);
            String httpURI = httpServletRequest.getRequestURI();
            Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, timestamp, fateManagerUserId, nonce, httpURI));
            log.info(
                    "shortLinkActivateCheckSignature Head Information | timestamp:{},fateManagerUserId:{},nonce:{},httpURI:{},httpBody:{},signature:{}",
                    timestamp, fateManagerUserId, nonce, httpURI, httpBody, signature
            );

            if (fateManagerUserStatus == null || 0 == fateManagerUserStatus.length) {
                throw new IllegalArgumentException();
            }

            // get data of the fate manager user
            FederatedFateManagerUserDo fateManagerUserDo = federatedFateManagerUserService.findFateManagerUser(fateManagerUserId);
            if (fateManagerUserDo == null) {
                return false;
            }

            // check fate manager user status
            for (int i : fateManagerUserStatus) {
                if (i != fateManagerUserDo.getStatus()) {
                    return false;
                }
            }

            String trueSignature = EncryptUtil.generateSignature(fateManagerUserId, timestamp, fateManagerUserId, nonce, httpURI, httpBody);

            log.info(
                    "shortLinkActivateCheckSignature True Information | timestamp:{},fateManagerUserId:{},nonce:{},httpURI:{},httpBody:{},signature:{}",
                    timestamp, fateManagerUserId, nonce, httpURI, httpBody, trueSignature
            );

            return StringUtils.equals(signature, trueSignature);
        } else {
            if (Dict.FATE_SITE_USER.equals(type)) {
                //check header parameters
                String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
                String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
                String partyId = httpServletRequest.getHeader(Dict.PARTY_ID);
                String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
                String fateManagerUserAppKey = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_APP_KEY);
                String appKey = httpServletRequest.getHeader(Dict.APP_KEY);
                String nonce = httpServletRequest.getHeader(Dict.NONCE);
                String httpURI = httpServletRequest.getRequestURI();

                log.info(
                        "Head Information | timestamp:{},partyId:{},fateManagerUserId:{},fateManagerUserAppKey:{},appKey:{},nonce:{},httpURI:{},httpBody:{},signature:{}",
                        timestamp, partyId, fateManagerUserId, fateManagerUserAppKey, appKey, nonce, httpURI, httpBody, signature
                );
                Preconditions.checkArgument(StringUtils.isNoneEmpty(fateManagerUserId, fateManagerUserAppKey, partyId, appKey, timestamp, nonce, httpURI));

                if (fateManagerUserStatus == null || 0 == fateManagerUserStatus.length) {
                    throw new IllegalArgumentException();
                }

                if (siteStatus == null || 0 == siteStatus.length) {
                    throw new IllegalArgumentException();
                }

                // get data of the fate manager user
                FederatedFateManagerUserDo fateManagerUser = federatedFateManagerUserService.findFateManagerUser(fateManagerUserId);
                if (fateManagerUser == null) {
                    return false;
                }

                // check fate manager user status
                boolean fateManagerUserStatusResult = false;
                for (int i : fateManagerUserStatus) {
                    if (i == fateManagerUser.getStatus()) {
                        fateManagerUserStatusResult = true;
                        break;
                    }
                }
                if (!fateManagerUserStatusResult) {
                    return false;
                }

                String fateManagerSecretInfoString = fateManagerUser.getSecretInfo();
                SecretInfo fateManagerSecretInfo = JSON.parseObject(fateManagerSecretInfoString, new TypeReference<SecretInfo>() {
                });
                String fateManagerUserKey = fateManagerSecretInfo.getKey();
                String fateManagerUserSecret = fateManagerSecretInfo.getSecret();

                if (!fateManagerUserAppKey.equals(fateManagerUserKey)) {
                    return false;
                }


                // get site information
                SiteDetailDto site = federatedSiteManagerService.findSiteByPartyId(Long.parseLong(partyId), appKey, siteStatus);

                if (site == null) {
                    return false;
                }

                // check site belong to institutions
                if (!fateManagerUser.getInstitutions().equals(site.getInstitutions())) {
                    return false;
                }


                SecretInfo siteSecretInfo = site.getSecretInfo();
                String siteKey = siteSecretInfo.getKey();
                //String siteSecret = siteSecretInfo.getSecret();

                if (!appKey.equals(siteKey)) {
                    return false;
                }

                String trueSignature = EncryptUtil.generateSignature(fateManagerUserSecret,
                        timestamp, partyId, fateManagerUserId, fateManagerUserAppKey, appKey, nonce, httpURI, httpBody);

                log.info(
                        "True Information | fateManagerUserSecret:{},timestamp:{},partyId:{},fateManagerUserId:{},fateManagerUserAppKey:{},appKey:{},nonce:{},httpURI:{},httpBody:{},trueSignature:{}",
                        fateManagerUserSecret, timestamp, partyId, fateManagerUserId, fateManagerUserAppKey, appKey, nonce, httpURI, httpBody, trueSignature
                );

                return StringUtils.equals(signature, trueSignature);
            } else {
                return false;
            }
        }

    }
}
