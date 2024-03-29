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

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@JsonPropertyOrder(alphabetic = true)

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("model bean for add")
public class ModelAddQo implements Serializable {
    @ApiModelProperty(value = "model name")
    private String installItems;

    @ApiModelProperty(value = "model version")
    private String version;

    @ApiModelProperty(value = "mode update status")
    private Integer updateStatus;

    @ApiModelProperty(value = "system type")
    private String type;

    @ApiModelProperty(value = "site id")
    private Long id;

    @ApiModelProperty(value = "update time")
    private Date updateTime;

    @ApiModelProperty(value = "model detective status")
    private Integer detectiveStatus;
}
