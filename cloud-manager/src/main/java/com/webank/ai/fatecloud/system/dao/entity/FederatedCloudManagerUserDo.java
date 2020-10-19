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
@ApiModel(value = "cloud manager user information")
@TableName("t_cloud_manager_user")
public class FederatedCloudManagerUserDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "cloud_manager_id", type = IdType.AUTO)
    private Long cloudManagerId;

    @ApiModelProperty(value = "user name")
    @TableField(value = "name")
    private String name;


    @ApiModelProperty(value = "admin user level")
    @TableField(value = "admin_level")
    private Integer adminLevel;

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
