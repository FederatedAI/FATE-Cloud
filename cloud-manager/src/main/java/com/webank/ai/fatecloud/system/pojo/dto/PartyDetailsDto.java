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

import com.webank.ai.fatecloud.system.dao.entity.RollSiteDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class PartyDetailsDto {
    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "party id value")
    private String partyId;

    @ApiModelProperty(value = "network access ")
    private String networkAccess;

    @ApiModelProperty(value = "secure status")
    private Integer secureStatus;

    @ApiModelProperty(value = "polling status")
    private Integer pollingStatus;

    @ApiModelProperty(value = "polling status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    private Date updateTime;

    @ApiModelProperty(value = "valid time")
    private Date validTime;

    @ApiModelProperty(value = "contains the current party roll site list")
    private List<RollSiteDo> rollSiteDoList;

    @ApiModelProperty(value = "exchange id")
    private Long exchangeId;

    @ApiModelProperty(value = "exchange name")
    private String exchangeName;

    @ApiModelProperty(value = "vip address access entrance")
    private String vipEntrance;
}
