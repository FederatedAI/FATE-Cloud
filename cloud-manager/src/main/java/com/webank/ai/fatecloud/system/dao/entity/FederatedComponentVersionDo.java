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
import com.webank.ai.fatecloud.system.pojo.qo.ComponentVersionAddQo;
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
@TableName("t_component_version")
@ApiModel(value = "component version do bean")
public class FederatedComponentVersionDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "component_id", type = IdType.AUTO)
    private Long componentId;

    @ApiModelProperty(value = "component name")
    @TableField(value = "component_name")
    private String componentName;

    @ApiModelProperty(value = "component version")
    @TableField(value = "component_version")
    private String componentVersion;

    @ApiModelProperty(value = "image repository")
    @TableField(value = "image_repository")
    private String imageRepository;

    @ApiModelProperty(value = "image tag")
    @TableField(value = "image_tag")
    private String imageTag;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "product primary key")
    @TableField(value = "product_id")
    private Long productId;


    public FederatedComponentVersionDo(ComponentVersionAddQo componentVersionAddQo) {
        this.componentName = componentVersionAddQo.getComponentName();
        this.componentVersion = componentVersionAddQo.getComponentVersion();
        this.imageRepository = componentVersionAddQo.getImageRepository();
        this.imageTag = componentVersionAddQo.getImageTag();
    }

}
