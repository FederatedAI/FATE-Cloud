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
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job summary for today, institutions dimension")
public class JobStatisticsSummaryTodayInstitutionsAllDto implements Serializable {
    @ApiModelProperty(value = "total institutions count")
    private Integer institutionsCount;

    @ApiModelProperty(value = "total failed job count")
    private Integer failedJobCount;

    @ApiModelProperty(value = "total successful job count")
    private Integer successJobCount;

    @ApiModelProperty(value = "total running job count")
    private Integer runningJobCount;

}
