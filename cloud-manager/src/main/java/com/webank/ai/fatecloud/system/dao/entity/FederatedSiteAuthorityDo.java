package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_federated_site_authority")
@ApiModel(value = "federated site authority information")
public class FederatedSiteAuthorityDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "authority_id", type = IdType.AUTO)
    private Long authorityId;

    @ApiModelProperty(value = "institutions name")
    @TableField(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "accessible institutions")
    @TableField(value = "authority_institutions")
    private String authorityInstitutions;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "admin activate status, 1:pending, 2:yes 3:no")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = " to show the generation of information, 1:current, 2:old")
    @TableField(value = "generation")
    private Integer generation;

    @ApiModelProperty(value = "insert sequence")
    @TableField(value = "sequence")
    private Long sequence;

}
