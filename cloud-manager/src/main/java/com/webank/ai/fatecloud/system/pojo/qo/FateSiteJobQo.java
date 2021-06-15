package com.webank.ai.fatecloud.system.pojo.qo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class FateSiteJobQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String job_id;
    private String institutions;
    private String site_name;
    private Long party_id;
    private String role;
    private String roles;
    private String other_party_id;
    private String other_institutions;
    private String job_type;
    private String status;
    private Long job_elapsed;
    private Long job_create_day_date;

    private Long create_date;
    private Long create_time;
    private Long update_time;
    private Long update_date;
    private String job_create_day;
    private Long job_create_time;
    private Long job_start_time;
    private Long job_end_time;
    private Long job_info;

}
