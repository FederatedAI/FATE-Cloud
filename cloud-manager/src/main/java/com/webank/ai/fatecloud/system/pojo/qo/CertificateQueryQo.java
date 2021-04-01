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
@ApiModel(value = "certificate query info")
public class CertificateQueryQo {
    private Long id;

    @ApiModelProperty(value = "certificate type", example = "3")
    private Long typeId;

    @ApiModelProperty(value = "certificate institution", example = "could")
    private String institution;

    @ApiModelProperty(value = "certificate status", example = "Valid")
    private String status;

    @ApiModelProperty(value = "Search for certificate ID or Site", example = "750200005a01")
    private String queryString;

    @ApiModelProperty(value = "certificate uniquely serial number(certificateID)", example = "750200005a01")
    private String serialNumber;

    @ApiModelProperty(value = "pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 20;
}
