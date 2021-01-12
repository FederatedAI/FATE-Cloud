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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@ApiModel(value = "jobs statistics for sites bean")
public class JobStatisticOfInstitutionsDimensionDto implements Serializable {

    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty(value = "successful job count for institutions")
    private String successJobCountForInstitutions;

    @ApiModelProperty(value = "failed job count for institutions")
    private String failedJobCountForInstitutions;

    @ApiModelProperty(value = "running job count for institutions")
    private String runningJobCountForInstitutions;

    @ApiModelProperty(value = "waiting job count for institutions")
    private String waitingJobCountForInstitutions;

}
