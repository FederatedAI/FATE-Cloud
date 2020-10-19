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
@ApiModel(value = "group details")
@TableName("t_federated_group_range_detail")
public class FederatedGroupDetailDo implements Serializable {
    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "group name")
    @TableField(value = "left_region")
    private Long leftRegion;

    @ApiModelProperty(value = "end")
    @TableField(value = "right_region")
    private Long rightRegion;

    @ApiModelProperty(value = "region size")
    @TableField(value = "size")
    private Long size;

    @ApiModelProperty(value = "group detail status")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "used party id num")
    @TableField(value = "use_tag")
    private Long useTag;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "group id")
    @TableField(value = "group_id")
    private Long groupId;
}
