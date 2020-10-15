package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.JobInformationQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_job_statistics")
@ApiModel(value = "jobs statistics for sites")
public class FederatedJobStatisticsDo implements Serializable {

    @ApiModelProperty(value = "guest id")
    @TableField(value = "site_guest_id")
    private String siteGuestId;

    @ApiModelProperty(value = "host id")
    @TableField(value = "site_host_id")
    private String siteHostId;

    @ApiModelProperty(value = "success count")
    @TableField(value = "job_success_count")
    private String jobSuccessCount;

    @ApiModelProperty(value = "failed count")
    @TableField(value = "job_failed_count")
    private String jobFailedCount;

    @ApiModelProperty(value = "finish date")
    @TableField(value = "job_finish_date")
    private String jobFinishDate;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private String createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private String updateTime;

    public FederatedJobStatisticsDo(JobInformationQo jobInformationQo) {
        this.
    }
}
