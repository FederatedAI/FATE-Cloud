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
package com.webank.ai.fatecloud.system.pojo.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.webank.ai.fatecloud.common.RangeInfo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "range info of group set ")
public class FederatedGroupSetRangeInfoDto implements Serializable {

    public FederatedGroupSetRangeInfoDto(FederatedGroupSetDo federatedGroupSetDo) {
        this.groupId = federatedGroupSetDo.getGroupId();
        this.groupName = federatedGroupSetDo.getGroupName();

        if (federatedGroupSetDo.getRangeInfo() != null) {
            String rangeInfoString = federatedGroupSetDo.getRangeInfo();
            this.rangeInfo = JSON.parseObject(rangeInfoString, new TypeReference<RangeInfo>() {
            });
        }
    }

    @ApiModelProperty(value = "group id")
    private Long groupId;

    @ApiModelProperty(value = "group name")
    private String groupName;

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;
}
