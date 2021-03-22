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

import com.webank.ai.fatecloud.system.dao.entity.FederatedComponentVersionDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionAddQo implements Serializable {

    @NotNull(message = "product name can't be null!")
    private String productName;

    @NotNull(message = "product version can't be null!")
    private String productVersion;

    private String imageName;

    private String imageDownloadUrl;

    private String packageName;

    private String packageDownloadUrl;

    private Integer publicStatus;

    private String kubernetesChart;

    @NotNull(message = "component version can't be null")
    private List<ComponentVersionAddQo> componentVersionAddQos;

}
