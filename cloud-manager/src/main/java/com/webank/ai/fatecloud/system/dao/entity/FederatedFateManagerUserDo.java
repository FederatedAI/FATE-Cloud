package com.webank.ai.fatecloud.system.dao.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "fate manager user information")
@TableName("t_fate_manager_user")
public class FederatedFateManagerUserDo {

    @ApiModelProperty(value = "primary key")
    @TableField(value = "fate_manager_id")
    private String fateManagerId;

    @ApiModelProperty(value = "institutions")
    @TableField(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "secret info")
    @TableField(value = "secret_info")
    private String secretInfo;

    @ApiModelProperty(value = "secret info")
    @TableField(value = "registration_link")
    private String registrationLink;

    @ApiModelProperty(value = "user creator")
    @TableField(value = "creator")
    private String creator;

    @ApiModelProperty(value = "function status")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;
}
