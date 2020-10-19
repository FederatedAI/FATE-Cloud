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
@ApiModel(value = "function information")
@TableName("t_federated_function")

public class FederatedFunctionDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "function_id", type = IdType.AUTO)
    private Long functionId;

    @ApiModelProperty(value = "function name")
    @TableField(value = "function_name")
    private String functionName;

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
