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
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("bean for find paged sites with models information")
public class SiteListWithModelsQo implements Serializable {

    @ApiModelProperty(value = "party id, site name")
    private String condition;

    @ApiModelProperty(value = "model version")
    private String version;

    @ApiModelProperty(value = "institutions list")
    private List<String> institutionsList;

    @ApiModelProperty(value = "role,0:all, 1:guest,2:host")
    private Integer role = 0;

    @ApiModelProperty(value = "site status,0 : all 1 : not joined,2 joined,3 : removed")
    private Integer status = 2;

    @ApiModelProperty("order field")
    private String orderField = "create_time";

    @ApiModelProperty("order type,1:asc,2:desc,default:desc")
    private Integer orderRule = 2;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;
}
