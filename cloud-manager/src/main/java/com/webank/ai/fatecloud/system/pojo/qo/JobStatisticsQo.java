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

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job information")
public class JobStatisticsQo implements Serializable {
    @JSONField(ordinal = 5)
    @NotNull(message = "site guest id can't be null!")
    @ApiModelProperty(value = "guest id")
    private Long siteGuestId;

    @JSONField(ordinal = 6)
    @NotNull(message = "site host id can't be null!")
    @ApiModelProperty(value = "host id")
    private Long siteHostId;

    @JSONField(ordinal = 3)
    @NotNull(message = "success count can't be null!")
    @ApiModelProperty(value = "success count")
    private Long jobSuccessCount;

    @JSONField(ordinal = 1)
    @NotNull(message = "failed count can't be null!")
    @ApiModelProperty(value = "failed count")
    private Long jobFailedCount;

    @JSONField(ordinal = 2)
    @NotNull(message = "running count can't be null!")
    @ApiModelProperty(value = "running count")
    private Long jobRunningCount;

    @JSONField(ordinal = 4)
    @NotNull(message = "finish date can't be null!")
    @ApiModelProperty(value = "finish date")
    private Long jobFinishDate;

}
