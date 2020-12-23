package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_job_statistics")
@ApiModel(value = "jobs statistics for sites")
public class FederatedJobStatisticsDo implements Serializable {

    @ApiModelProperty(value = "guest id")
    @TableField(value = "site_guest_id")
    private Long siteGuestId;

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
    }
}
