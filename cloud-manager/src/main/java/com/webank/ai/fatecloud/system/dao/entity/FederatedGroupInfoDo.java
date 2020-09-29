package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "group info of site")
public class FederatedGroupInfoDo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "primary key")
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    @ApiModelProperty(value = "group name")
    @TableField(value = "group_name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    @TableField(value = "role")
    private Integer role;

    @ApiModelProperty(value = "group status, 1:valid, 2:delete")
    @TableField(value = "status")
    private Integer status;

}
