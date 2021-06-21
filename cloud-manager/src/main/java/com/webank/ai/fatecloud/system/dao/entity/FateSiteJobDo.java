package com.webank.ai.fatecloud.system.dao.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.webank.ai.fatecloud.system.pojo.qo.FateSiteJobQo;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@JsonPropertyOrder(alphabetic = true)

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "fate site job information", description = "information for each job")
@TableName("t_fate_site_job")
public class FateSiteJobDo implements Serializable {
    private static final long serialVersionUID = -1L;

    @TableField(value = "job_id")
    private String jobId;
    @TableField(value = "institutions")
    private String institutions;
    @TableField(value = "site_name")
    private String siteName;
    @TableField(value = "party_id")
    private Long partyId;
    @TableField(value = "role")
    private String role;
    @TableField(value = "roles")
    private String roles;
    @TableField(value = "other_party_id")
    private String otherPartyId;
    @TableField(value = "other_institutions")
    private String otherInstitutions;
    @TableField(value = "job_type")
    private String jobType;
    @TableField(value = "status")
    private String status;
    @TableField(value = "job_elapsed")
    private Long jobElapsed;
    @TableField(value = "job_create_day_date")
    private Date jobCreateDayDate;

    @TableField(value = "create_date")
    private Date createDate;
    @TableField(value = "create_time")
    private Long createTime;
    @TableField(value = "update_time")
    private Long updateTime;
    @TableField(value = "update_date")
    private Date updateDate;
    @TableField(value = "job_create_day")
    private String jobCreateDay;
    @TableField(value = "job_create_time")
    private Long jobCreateTime;
    @TableField(value = "job_start_time")
    private Long jobStartTime;
    @TableField(value = "job_end_time")
    private Long jobEndTime;
    @TableField(value = "job_info")
    private Long jobInfo;

    public FateSiteJobDo(FateSiteJobQo fateSiteJobQo) {
        this.jobId = fateSiteJobQo.getJob_id();
        this.institutions = fateSiteJobQo.getInstitutions();
        this.siteName = fateSiteJobQo.getSite_name();
        this.partyId = fateSiteJobQo.getParty_id();
        this.role = fateSiteJobQo.getRole();
        this.roles = fateSiteJobQo.getRoles();
        this.otherPartyId = fateSiteJobQo.getOther_party_id();
        this.otherInstitutions = fateSiteJobQo.getOther_institutions();
        this.jobType = fateSiteJobQo.getJob_type();
        this.status = fateSiteJobQo.getStatus();
        this.jobElapsed = fateSiteJobQo.getJob_elapsed();
        if (fateSiteJobQo.getJob_create_day_date() != null) {
            this.jobCreateDayDate = new Date(fateSiteJobQo.getJob_create_day_date());
        }
        if (fateSiteJobQo.getCreate_date() != null) {
            this.createDate = new Date(fateSiteJobQo.getCreate_date());
        }
        this.createTime = fateSiteJobQo.getCreate_time();
        this.updateTime = fateSiteJobQo.getUpdate_time();
        if (fateSiteJobQo.getUpdate_date() != null) {
            this.updateDate = new Date(fateSiteJobQo.getUpdate_date());
        }
        this.jobCreateDay = fateSiteJobQo.getJob_create_day();
        this.jobCreateTime = fateSiteJobQo.getCreate_time();
        this.jobStartTime = fateSiteJobQo.getJob_start_time();
        this.jobEndTime = fateSiteJobQo.getJob_end_time();
        this.jobInfo = fateSiteJobQo.getJob_info();
    }
}
