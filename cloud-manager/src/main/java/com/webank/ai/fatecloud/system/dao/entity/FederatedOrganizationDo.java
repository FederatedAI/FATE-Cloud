package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
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
@TableName("t_federated_organization")
@ApiModel(value = "federated_organization")
public class FederatedOrganizationDo implements Serializable  {

    private static final long serialVersionUID = -1L;

    public FederatedOrganizationDo(FederatedOrganizationRegisterQo federatedOrganizationRegisterQo) {
        this.name = federatedOrganizationRegisterQo.getName();
        this.institution = federatedOrganizationRegisterQo.getInstitution();
    }

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "Organization name")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "Institution name")
    @TableField(value = "institution")
    private String institution;

    @ApiModelProperty(value = "Federated Organization Status,0:unvalid,1:valid")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "Federated Organization Descriptions")
    @TableField(value = "descriptions")
    private String descriptions;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

}
