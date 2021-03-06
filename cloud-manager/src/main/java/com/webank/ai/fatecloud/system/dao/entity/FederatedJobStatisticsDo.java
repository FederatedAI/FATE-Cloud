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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
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
@TableName("t_job_statistics")
@ApiModel(value = "jobs statistics for sites")
public class FederatedJobStatisticsDo implements Serializable {

    @ApiModelProperty(value = "guest site belongs to institutions")
    @TableField(value = "site_guest_institutions")
    private String siteGuestInstitutions;

    @ApiModelProperty(value = "guest site Name")
    @TableField(value = "site_guest_name")
    private String siteGuestName;

    @ApiModelProperty(value = "guest id")
    @TableField(value = "site_guest_id")
    private Long siteGuestId;

    @ApiModelProperty(value = "host site belongs to institutions")
    @TableField(value = "site_host_institutions")
    private String siteHostInstitutions;

    @ApiModelProperty(value = "host site Name")
    @TableField(value = "site_host_name")
    private String siteHostName;

    @ApiModelProperty(value = "host id")
    @TableField(value = "site_host_id")
    private Long siteHostId;

    @ApiModelProperty(value = "success count")
    @TableField(value = "job_success_count")
    private Long jobSuccessCount;

    @ApiModelProperty(value = "failed count")
    @TableField(value = "job_failed_count")
    private Long jobFailedCount;

    @ApiModelProperty(value = "running count")
    @TableField(value = "job_running_count")
    private Long jobRunningCount;

    @ApiModelProperty(value = "waiting count")
    @TableField(value = "job_waiting_count")
    private Long jobWaitingCount;

    @ApiModelProperty(value = "finish date")
    @TableField(value = "job_finish_date")
    private Date jobFinishDate;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    public FederatedJobStatisticsDo(JobStatisticsQo jobStatisticsQo) {
        this.siteGuestId = jobStatisticsQo.getSiteGuestId();
        this.siteHostId = jobStatisticsQo.getSiteHostId();
        this.jobSuccessCount = jobStatisticsQo.getJobSuccessCount();
        this.jobFailedCount = jobStatisticsQo.getJobFailedCount();
        this.jobRunningCount = jobStatisticsQo.getJobRunningCount();
        this.jobFinishDate = new Date(jobStatisticsQo.getJobFinishDate());
        this.jobWaitingCount=jobStatisticsQo.getJobWaitingCount();
    }
}
