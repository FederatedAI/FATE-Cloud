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
@TableName("t_federated_ip_manager")
@ApiModel(value = "Federated Ip Manager")
public class FederatedIpManagerDo implements Serializable  {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "case id")
    @TableField(value = "case_id")
    private String caseId;

    @ApiModelProperty(value = "site name")
    @TableField(value = "site_name")
    private String siteName;

    @ApiModelProperty(value = "group_id")
    @TableField(value = "group_id")
    private Long groupId;

    @ApiModelProperty(value = "site belongs to institutions")
    @TableField(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "site partyid")
    @TableField(value = "party_id")
    private Long partyId;

    @ApiModelProperty(value = "role,1:guest,2:host")
    @TableField(value = "role")
    private Integer role;

    @ApiModelProperty(value = "network access entrances")
    @TableField(value = "network_access_entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    @TableField(value = "network_access_exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "network access entrances_old")
    @TableField(value = "network_access_entrances_old")
    private String networkAccessEntrancesOld;

    @ApiModelProperty(value = "network access exits_old")
    @TableField(value = "network_access_exits_old")
    private String networkAccessExitsOld;

    @ApiModelProperty(value = "exchange name")
    @TableField(value = "exchange_name")
    private String exchangeName;

    @ApiModelProperty(value = "network access exits_old")
    @TableField(value = "exchange_name_old")
    private String exchangeNameOld;

    @ApiModelProperty(value = "exchange vip entrance")
    @TableField(value = "vip_entrance")
    private String vipEntrance;

    @ApiModelProperty(value = "exchange vip entrance old")
    @TableField(value = "vip_entrance_old")
    private String vipEntranceOld;

    @ApiModelProperty(value = "site party secure status")
    @TableField(value = "secure_status")
    private Integer secureStatus;

    @ApiModelProperty(value = "site party secure status old")
    @TableField(value = "secure_status_old")
    private Integer secureStatusOld;

    @ApiModelProperty(value = "site party polling status")
    @TableField(value = "polling_status")
    private Integer pollingStatus;

    @ApiModelProperty(value = "site party polling status old")
    @TableField(value = "polling_status_old")
    private Integer pollingStatusOld;

    @ApiModelProperty(value = "log deal status,0:no deal,1:agreed,2:rejected")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

}
