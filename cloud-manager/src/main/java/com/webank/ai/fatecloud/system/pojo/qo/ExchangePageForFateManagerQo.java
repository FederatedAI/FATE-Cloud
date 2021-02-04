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

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("find exchange page for fate manager")
public class ExchangePageForFateManagerQo implements Serializable {

    @JSONField(ordinal = 3)
    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @JSONField(ordinal = 1)
    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @JSONField(ordinal = 2)
    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

}
