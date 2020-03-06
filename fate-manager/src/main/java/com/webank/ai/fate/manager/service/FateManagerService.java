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
package com.webank.ai.fate.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Preconditions;
import com.webank.ai.fate.manager.common.CommonResponse;
import com.webank.ai.fate.manager.common.enums.NodeStatusEnum;
import com.webank.ai.fate.manager.common.enums.RetEnum;
import com.webank.ai.fate.manager.common.enums.RoleTypeEnum;
import com.webank.ai.fate.manager.dao.entity.NodeDo;
import com.webank.ai.fate.manager.dao.mapper.NodeDao;
import com.webank.ai.fate.manager.pojo.dto.AccountDto;
import com.webank.ai.fate.manager.pojo.dto.NodeDto;
import com.webank.ai.fate.manager.pojo.qo.CheckNodeQo;
import com.webank.ai.fate.manager.pojo.qo.NodeQo;
import com.webank.ai.fate.manager.utils.CommUtil;
import com.webank.ai.fate.manager.utils.Dict;
import com.webank.ai.fate.manager.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FateManagerService {

    @Autowired
    HttpUtil httpUtil;

    @Autowired
    NodeDao nodeDao;

    public CommonResponse<NodeDto> getNodeInfo() {
        try {
            NodeDo nodeDo = nodeDao.selectOne(new QueryWrapper<>());
            if (nodeDo == null) {
                return CommonResponse.success(null);
            }
            JSONObject data = new JSONObject();
            data.put("partyId", nodeDo.getPartyId());
            Map<String, String> headers = CommUtil.getHeaderInfo(nodeDo, data.toJSONString(), Dict.CloudStationQueryUri);
            String result = httpUtil.doPost(nodeDo.getHostDomain() + Dict.CloudStationQueryUri, data.toJSONString(), headers);

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger("code").equals(RetEnum.SUCCESS.getValue())) {
                JSONObject dataObject = jsonObject.getJSONObject("data");

                NodeDto nodeDto = new NodeDto();
                nodeDto.setPartyId(dataObject.getString("partyId"));
                nodeDto.setExportUrl(dataObject.getString("exportUrl"));
                nodeDto.setPortalUrl(dataObject.getString("portalUrl"));
                nodeDto.setAppKey(dataObject.getString("key"));
                nodeDto.setAppSecret(dataObject.getString("secret"));
                nodeDto.setDetectiveStatusPair(dataObject.getJSONObject("detectiveStatusVO"));
                nodeDto.setNodePublicPair(dataObject.getJSONObject("nodePublicVO"));
                nodeDto.setNodeStatusPair(dataObject.getJSONObject("nodeStatusVO"));
                nodeDto.setRoleTypePair(dataObject.getJSONObject("roleTypeVO"));
                nodeDto.setFedratrionUrl(dataObject.getString("organizationDesc"));

                return CommonResponse.success(nodeDto);
            } else {
                return CommonResponse.error(jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            return CommonResponse.error("getNodeInfo failed");
        }
    }

    public CommonResponse<JSONObject> addFedNode(NodeQo nodeQo) {
        try {
            Preconditions.checkArgument(StringUtils.isNoneEmpty(nodeQo.getPartyId(), nodeQo.getFedratrionUrl(), nodeQo.getExportUrl(), nodeQo.getPortalUrl(), nodeQo.getAppKey(), nodeQo.getAppSecret(), nodeQo.getRoleType().toString(), nodeQo.getNodePublic().toString()));
        } catch (Exception e) {
            return CommonResponse.error("request param error");
        }
        NodeDo nodeDo = nodeDao.selectOne(new QueryWrapper<>());
        boolean bFirst = true;
        if (nodeDo != null) {
            bFirst = false;
            if (nodeDo.getPartyId().equals(nodeQo.getPartyId()) && nodeDo.getNodeStatus().equals(NodeStatusEnum.NODE_REMOVE.getValue()))
                return CommonResponse.error("This partyid has been removed, please register with another partyid!");
        } else {
            nodeDo = new NodeDo();
        }

        JSONObject postData = new JSONObject();
        postData.put("partyId", nodeQo.getPartyId());
        postData.put("exportUrl", nodeQo.getExportUrl());
        postData.put("portalUrl", nodeQo.getPortalUrl());
        postData.put("roleType", nodeQo.getRoleType());
        postData.put("nodePublic", nodeQo.getNodePublic());
        postData.put("organizationDesc", nodeQo.getFedratrionUrl());

        nodeDo.setPartyId(nodeQo.getPartyId());
        nodeDo.setAppSecret(nodeQo.getAppSecret());
        nodeDo.setAppKey(nodeQo.getAppKey());
        nodeDo.setRoleType(nodeQo.getRoleType());
        nodeDo.setPortalUrl(nodeQo.getPortalUrl());
        nodeDo.setExportUrl(nodeQo.getExportUrl());
        nodeDo.setFedOrgUrl(nodeQo.getFedratrionUrl());

        Map<String, String> headers = CommUtil.getHeaderInfo(nodeDo, postData.toJSONString(), Dict.CloudStationRegisterUri);
        String result = httpUtil.doPost(nodeQo.getFedratrionUrl(), postData.toJSONString(), headers);

        JSONObject resultObject = JSONObject.parseObject(result);
        if (resultObject.getInteger("code").equals(RetEnum.SUCCESS.getValue())) {
            nodeDo.setNodeStatus(NodeStatusEnum.NODE_JOIN.getValue());
            if (bFirst) {
                nodeDo.setNode(RoleTypeEnum.GUEST.toString());
                int idx = nodeQo.getFedratrionUrl().indexOf(Dict.CloudStationRegisterUri);
                nodeDo.setHostDomain(nodeQo.getFedratrionUrl().substring(0, idx));
                nodeDao.insert(nodeDo);
            } else {
                QueryWrapper<NodeDo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("node", RoleTypeEnum.GUEST.toString());
                nodeDao.update(nodeDo, queryWrapper);
            }
            return CommonResponse.success("success");
        } else {
            return CommonResponse.error(resultObject.getString("msg"));
        }
    }

    public CommonResponse<JSONObject> checkNodeInfo(CheckNodeQo checkNodeQo) {
        try {
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            JSONObject srcBody = new JSONObject();

            NodeDo nodeDo = nodeDao.selectOne(new QueryWrapper<>());
            if (nodeDo == null) {
                return CommonResponse.error("no partyId info");
            }

            NodeDo srcNodeDo = new NodeDo();
            srcNodeDo.setPartyId(checkNodeQo.getPartyId());
            srcNodeDo.setAppKey(checkNodeQo.getAppKey());
            srcNodeDo.setAppSecret(checkNodeQo.getAppSecret());
            srcNodeDo.setRoleType(checkNodeQo.getRole().equals(RoleTypeEnum.GUEST.toString()) ? RoleTypeEnum.GUEST.getValue() : RoleTypeEnum.HOST.getValue());
            srcBody.put("partyId", checkNodeQo.getPartyId());
            Map<String, String> srtHeaders = CommUtil.getHeaderInfo(srcNodeDo, srcBody.toJSONString(), Dict.CloudStationCheckUri);

            header.put("APP_KEY", srtHeaders.get("APP_KEY"));
            header.put("TIMESTAMP", srtHeaders.get("TIMESTAMP"));
            header.put("SIGNATURE", srtHeaders.get("SIGNATURE"));
            header.put("PARTY_ID", srtHeaders.get("PARTY_ID"));
            header.put("NONCE", srtHeaders.get("NONCE"));
            header.put("ROLE", srtHeaders.get("ROLE"));

            data.put("HTTP_HEADER", header);
            data.put("HTTP_URI", Dict.CloudStationCheckUri);
            data.put("HTTP_BODY", srcBody);

            Map<String, String> dstHeaders = CommUtil.getHeaderInfo(nodeDo, data.toJSONString(), Dict.CloudStationCheckUri);
            String result = httpUtil.doPost(nodeDo.getHostDomain() + Dict.CloudStationCheckUri, data.toJSONString(), dstHeaders);

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger("code").equals(RetEnum.SUCCESS.getValue())) {
                return CommonResponse.success(null);
            } else {
                return CommonResponse.error(jsonObject.getInteger("code"),jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            return CommonResponse.error("checkNodeInfo failed");
        }
    }

    public CommonResponse<AccountDto> getAccountINfo(String partyId) {
        QueryWrapper<NodeDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);

        NodeDo nodeDo = nodeDao.selectOne(queryWrapper);
        if (nodeDo == null) {
            return CommonResponse.error("This partyId not exists!");
        }
        AccountDto accountDto = new AccountDto();
        accountDto.setAppKey(nodeDo.getAppKey());
        accountDto.setAppSecret(nodeDo.getAppSecret());

        return CommonResponse.success(accountDto);
    }
}
