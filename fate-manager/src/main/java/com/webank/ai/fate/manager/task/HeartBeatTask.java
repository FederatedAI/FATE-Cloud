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
package com.webank.ai.fate.manager.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fate.manager.common.enums.NodeStatusEnum;
import com.webank.ai.fate.manager.common.enums.RetEnum;
import com.webank.ai.fate.manager.dao.entity.NodeDo;
import com.webank.ai.fate.manager.dao.mapper.NodeDao;
import com.webank.ai.fate.manager.utils.CommUtil;
import com.webank.ai.fate.manager.utils.Dict;
import com.webank.ai.fate.manager.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableScheduling
public class HeartBeatTask {

    @Autowired
    HttpUtil httpUtil;

    @Autowired
    NodeDao nodeDao;

    @Scheduled(fixedDelay = 5000)
    public void heartTask() {
        NodeDo nodeDo = nodeDao.selectOne(new QueryWrapper<>());
        if (nodeDo == null) {
            return;
        }
        if (nodeDo.getNodeStatus().equals(NodeStatusEnum.NODE_JOIN.getValue())) {
            JSONObject body = new JSONObject();
            body.put("partyId", nodeDo.getPartyId());
            Map<String, String> headers = CommUtil.getHeaderInfo(nodeDo, body.toJSONString(), Dict.CloudStationHeartUri);
            String result = httpUtil.doPost(nodeDo.getHostDomain() + Dict.CloudStationHeartUri, body.toJSONString(), headers);

            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer code = jsonObject.getInteger("code");
            if (code.equals(RetEnum.STATION_REGISTER_ERROR.getValue())||code.equals(RetEnum.STATION_REMOVE.getValue())) {
                nodeDo.setNodeStatus(Integer.valueOf(RetEnum.getEnumByType(code).toString()));
                QueryWrapper<NodeDo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("party_id", nodeDo.getPartyId());
                nodeDao.update(nodeDo, queryWrapper);
            }
        }
        System.out.println("nodeStatus:" + NodeStatusEnum.getEnumByType(nodeDo.getNodeStatus()).toString());
    }
}
