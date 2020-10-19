package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.system.dao.entity.FederatedOrganizationDo;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "Federated Organization Details")
public class FederatedOrganizationDto extends FederatedOrganizationRegisterQo implements Serializable {
    public FederatedOrganizationDto(FederatedOrganizationDo federatedOrganizationDo) {
        this.name = federatedOrganizationDo.getName();
        this.institution = federatedOrganizationDo.getInstitution();
        if (federatedOrganizationDo.getCreateTime() != null) {
            this.createTime = federatedOrganizationDo.getCreateTime().getTime();
        }
    }

    @ApiModelProperty(value = "create time")
    private Long createTime;
}
