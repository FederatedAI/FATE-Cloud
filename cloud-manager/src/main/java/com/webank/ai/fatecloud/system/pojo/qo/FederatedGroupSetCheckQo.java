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
package com.webank.ai.fatecloud.system.pojo.qo;

import com.webank.ai.fatecloud.common.RangeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "Check Group Set")
public class FederatedGroupSetCheckQo implements Serializable {

    public FederatedGroupSetCheckQo(FederatedGroupSetAddQo federatedGroupSetAddQo) {
        this.rangeInfo = federatedGroupSetAddQo.getRangeInfo();
        this.groupId = federatedGroupSetAddQo.getGroupId();
    }

    public FederatedGroupSetCheckQo(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        this.rangeInfo = federatedGroupSetUpdateQo.getRangeInfo();
        this.groupId = federatedGroupSetUpdateQo.getGroupId();
    }

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;

    @ApiModelProperty(value = "group id")
    private Long groupId;

}
