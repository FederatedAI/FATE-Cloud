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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "exchange routing party info update")
public class PartyUpdateQo {
    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "party id value")
    private Long partyId;

    @ApiModelProperty(value = "network access entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "exchange id")
    private Long exchangeId;

    @ApiModelProperty(value = "secure status")
    private Integer secureStatus;

    @ApiModelProperty(value = "polling status")
    private Integer pollingStatus;

    @ApiModelProperty(value = "status")
    private Integer status;
}
