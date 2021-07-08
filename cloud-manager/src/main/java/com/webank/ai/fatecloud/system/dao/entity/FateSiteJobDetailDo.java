package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "fate site job details information", description = "information for each job")
@TableName("t_fate_site_job_detail")
public class FateSiteJobDetailDo implements Serializable {

    private static final long serialVersionUID = -1L;

    @TableField(value = "detail_job_id")
    private String detailJobId;
    @TableField(value = "detail_institutions")
    private String detailInstitutions;
    @TableField(value = "detail_site_name")
    private String detailSiteName;
    @TableField(value = "detail_party_id")
    private Long detailPartyId;
    @TableField(value = "detail_role")
    private String detailRole;
    @TableField(value = "detail_status")
    private String detailStatus;
    @TableField(value = "detail_job_type")
    private String detailJobType;
    @TableField(value = "detail_job_create_day_date")
    private Date detailJobCreateDayDate;
    @TableField(value = "detail_create_time")
    private Date detailCreateTime;
    @TableField(value = "detail_update_time")
    private Date detailUpdateTime;

}
