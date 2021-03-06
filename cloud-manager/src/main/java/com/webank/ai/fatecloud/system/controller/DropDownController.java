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

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.EnumConvert;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.qo.DropDownVersionQo;
import com.webank.ai.fatecloud.system.service.facade.DropDownServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dropdown")
@Api(tags = "DropDownController", description = "About Enum Drop Down List")
public class DropDownController {

    @Autowired
    EnumConvert enumConvert;

    @Autowired
    DropDownServiceFacade dropDownServiceFacade;


    @GetMapping(value = "/all")
    @ApiOperation(value = "Drop Down All List")
    public CommonResponse getDropDownAllList() throws ReflectiveOperationException {
        Map<String, Object> all = enumConvert.all();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, all);

    }

    @PostMapping(value = "/version")
    @ApiOperation(value = "Drop Down Fate Version List")
    public CommonResponse<List<String>> getDropDownVersionList(@RequestBody DropDownVersionQo dropDownVersionQo) {

        return dropDownServiceFacade.getDropDownVersionList(dropDownVersionQo);
    }
}

