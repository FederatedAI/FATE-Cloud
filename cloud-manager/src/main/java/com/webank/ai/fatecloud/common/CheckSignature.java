package com.webank.ai.fatecloud.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.service.impl.FederatedFateManagerUserService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

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


//    public boolean checkFateManagerUserActivateAuthority(HttpServletRequest httpServletRequest, String httpBody, int... status) {
//        if (checksignature) {
//            String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
//            String fateManagerId = httpServletRequest.getHeader(Dict.FATE_MANAGER_ID);
//            String appKey = httpServletRequest.getHeader(Dict.APP_KEY);
//            String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
//            String nonce = httpServletRequest.getHeader(Dict.NONCE);
//            String httpURI = httpServletRequest.getRequestURI();
//            Preconditions.checkArgument(StringUtils.isNoneEmpty(signature, fateManagerId, appKey, timestamp, nonce, httpURI));
//
//            FederatedFateManagerUserDo fateManagerUser = federatedFateManagerUserService.findFateManagerUser(fateManagerId);
//            if (fateManagerUser == null) {
//                return false;
//            }
//
//            boolean statusResult = false;
//            for (int i : status) {
//                if (i == fateManagerUser.getStatus()) {
//                    statusResult = true;
//                    break;
//                }
//            }
//            if (!statusResult) {
//                return false;
//            }
//
//            String secretInfoString = fateManagerUser.getSecretInfo();
//            SecretInfo secretInfo = JSON.parseObject(secretInfoString, new TypeReference<SecretInfo>() {
//            });
//            String key = secretInfo.getKey();
//            String secret = secretInfo.getSecret();
//
//            String headSignature = EncryptUtil.generateSignature(secret, fateManagerId, appKey, timestamp, nonce, httpURI, httpBody);
//            String trueSignature = EncryptUtil.generateSignature(secret, fateManagerId, key, timestamp, nonce, httpURI, httpBody);
//
//            log.info("Request Signature: {}", signature);
//            log.info("Head Signature:    {} | secret:{},fateManagerId:{},appKey:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", headSignature, secret, fateManagerId, appKey, timestamp, nonce, httpURI, httpBody);
//            log.info("True Signature:    {} | secret:{},fateManagerId:{},   key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{}", trueSignature, secret, fateManagerId, key, timestamp, nonce, httpURI, httpBody);
//
//            return StringUtils.equals(signature, trueSignature) && StringUtils.equals(headSignature, trueSignature);
//        }
//        return true;
//    }

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
}
