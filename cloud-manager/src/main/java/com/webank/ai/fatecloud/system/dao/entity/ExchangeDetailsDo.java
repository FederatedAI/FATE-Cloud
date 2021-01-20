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
package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "exchange details information")
@TableName("t_exchange_details")
public class ExchangeDetailsDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "exchange_details_id", type = IdType.AUTO)
    private Long exchangeDetailsId;

    @ApiModelProperty(value = "exchange name")
    @TableField(value = "party_id")
    private String partyId;

    @ApiModelProperty(value = "network access ")
    @TableField(value = "network_access")
    private String networkAccess;

    @ApiModelProperty(value = "primary key")
    @TableId(value = "batch")
    private Long batch;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "exchange id")
    @TableId(value = "exchange_id")
    private Long exchangeId;

}