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
import com.webank.ai.fatecloud.system.pojo.qo.SiteAddQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_federated_site_manager")
@ApiModel(value = "Federated Site Manager")
public class FederatedSiteManagerDo  implements Serializable {

    public FederatedSiteManagerDo(SiteAddQo siteAddQo){
        this.siteName=siteAddQo.getSiteName();
        this.institutions=siteAddQo.getInstitutions();
        this.partyId=siteAddQo.getPartyId();
        this.groupId=siteAddQo.getGroupId();
        this.networkAccessEntrances=siteAddQo.getNetworkAccessEntrances();
        this.networkAccessExits=siteAddQo.getNetworkAccessExits();
    }

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "site name")
    @TableField(value = "site_name")
    private String siteName;

    @ApiModelProperty(value = "site partyid")
    @TableField(value = "party_id")
    private Long partyId;

    @ApiModelProperty(value = "site appkey && secret")
    @TableField(value = "secret_info")
    private String secretInfo;

    @ApiModelProperty(value = "federated registration link")
    @TableField(value = "registration_link")
    private String registrationLink;

    @ApiModelProperty(value = "network access entrances")
    @TableField(value = "network_access_entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    @TableField(value = "network_access_exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "site belongs to institutions")
    @TableField(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "fate version")
    @TableField(value = "fate_version")
    private String fateVersion;

    @ApiModelProperty(value = "fate component version")
    @TableField(value = "component_version")
    private String componentVersion;

    @ApiModelProperty(value = "fate serving version")
    @TableField(value = "fate_serving_version")
    private String fateServingVersion;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "activation Time")
    @TableField(value = "activation_time")
    private Date activationTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "site detective status")
    @TableField(value = "detective_status")
    private Integer detectiveStatus;

    @ApiModelProperty(value = "site detective time")
    @TableField(value = "last_detective_time")
    private Date lastDetectiveTime;

    @ApiModelProperty(value = "party id  group")
    @TableField(value = "group_id")
    private Long groupId;

    @TableField(exist = false)
    private FederatedGroupSetDo federatedGroupSetDo;

    @TableField(exist = false)
    private List<FederatedSiteModelDo> federatedSiteModelDos;
}
