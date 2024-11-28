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

import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "certificate info", description = "cloud certificate base info")
@TableName("t_cloud_certificate_manager")
public class FederatedCloudCertificateDo {
    @ApiModelProperty(value = "index")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "certificate type id", example = "1")
    @TableField(value = "type_id")
    private Long typeId;

    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String certificateType;

    @ApiModelProperty(value = "validity { 2021-01-01~2021-02-02 }", example = "2021-01-01~2021-02-02")
    @TableField(value = "validity")
    private String validity;

    @ApiModelProperty(value = "organization name", example = "manager")
    @TableField(value = "organization")
    private String organization;

    @ApiModelProperty(value = "certificate institution", example = "could")
    @TableField(value = "institution")
    private String institution;

    @ApiModelProperty(value = "site authority", example = "8888,7777,9999")
    @TableField(value = "site_authority")
    private String siteAuthority;

    @TableField(value = "status")
    @ApiModelProperty(hidden = true)
    private String status;

    @ApiModelProperty(value = "notes", example = "this is a notes")
    @TableField(value = "notes")
    private String notes;

    @ApiModelProperty(value = "certificate holder domain name", example = "fate.com,chain.net")
    @TableField(value = "dns_name")
    private String dnsName;

    @ApiModelProperty(value = "certificate uniquely serial number, publish post generation", hidden = true)
    @TableField(value = "serial_number")
    private String serialNumber;

    @TableField(value = "create_date")
    @ApiModelProperty(hidden = true)
    private Date createDate;

    @TableField(value = "update_date")
    @ApiModelProperty(hidden = true)
    private Date updateDate;

    public FederatedCloudCertificateDo(Long id, Long typeId, String institution, String status, String serialNumber) {
        this.id = id;
        this.status = status;
        this.institution = institution;
        this.serialNumber = serialNumber;
        this.typeId = typeId;
    }
}
