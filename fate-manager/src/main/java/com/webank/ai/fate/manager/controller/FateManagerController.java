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
package com.webank.ai.fate.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.webank.ai.fate.manager.common.CommonResponse;
import com.webank.ai.fate.manager.pojo.dto.AccountDto;
import com.webank.ai.fate.manager.pojo.dto.NodeDto;
import com.webank.ai.fate.manager.pojo.qo.CheckNodeQo;
import com.webank.ai.fate.manager.pojo.qo.NodeQo;
import com.webank.ai.fate.manager.service.FateManagerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@Slf4j
@RestController
@Data
public class FateManagerController {

    @Autowired
    FateManagerService fateManagerService;

    @GetMapping(value = "/node/management/query")
    public CommonResponse<NodeDto> getNodeInfo() {
        try {
            return fateManagerService.getNodeInfo();
        } catch (Exception e) {
            log.error("getNodeDetail failed ,error is {}", e);
            return CommonResponse.error("getNodeDetail failed");
        }
    }

    @PostMapping(value = "/node/management/update")
    public CommonResponse<JSONObject> addFedNode(@RequestBody NodeQo nodeQo) {
        try {
            return fateManagerService.addFedNode(nodeQo);
        } catch (Exception e) {
            log.error("addFedNode failed ,error is {}", e);
            return CommonResponse.error("addFedNode failed");
        }
    }

    @PostMapping(value = "/node/management/check")
    public CommonResponse<JSONObject> checkNodeInfo(@RequestBody CheckNodeQo checkNodeQo) {
        try {
            return fateManagerService.checkNodeInfo(checkNodeQo);
        } catch (Exception e) {
            log.error("checkNodeInfo failed ,error is {}", e);
            return CommonResponse.error("checkNodeInfo failed");
        }
    }

    @GetMapping(value = "/node/info")
    public CommonResponse<AccountDto> getAccountINfo(@RequestParam String partyId) {
        try {
            return fateManagerService.getAccountINfo(partyId);
        } catch (Exception e) {
            log.error("getAccountINfo failed ,error is {}", e);
            return CommonResponse.error("getAccountINfo failed");
        }
    }
}
