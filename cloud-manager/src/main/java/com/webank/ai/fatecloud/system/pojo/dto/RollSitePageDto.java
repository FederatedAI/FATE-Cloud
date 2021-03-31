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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.webank.ai.fatecloud.system.dao.entity.PartyDo;
import com.webank.ai.fatecloud.system.dao.entity.RollSiteDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "roll site details with count")
public class RollSitePageDto   {

    @ApiModelProperty(value = " published party count")
    private Integer count;

    @ApiModelProperty(value = "roll site count")
    private String status;

    @ApiModelProperty(value = "primary key")
    public Long rollSiteId;

    @ApiModelProperty(value = "network access ")
    public String networkAccess;

    @ApiModelProperty(value = "create time")
    public Date createTime;

    @ApiModelProperty(value = "update time")
    public Date updateTime;

    @ApiModelProperty(value = "exchange id")
    public Long exchangeId;

    public List<PartyDo> partyDos;

    public RollSitePageDto(RollSiteDo rollSiteDo){
        this.rollSiteId=rollSiteDo.getRollSiteId();
        this.networkAccess=rollSiteDo.getNetworkAccess();
        this.createTime=rollSiteDo.getCreateTime();
        this.updateTime=rollSiteDo.getUpdateTime();
        this.exchangeId=rollSiteDo.getRollSiteId();
        this.partyDos=rollSiteDo.getPartyDos();
    }

}
