package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "institution info for fate manager")
public class InstitutionsForFateDto implements Serializable {

    @ApiModelProperty("institution name")
    private String institutions;

    @ApiModelProperty("institution status: 1 not authority, 2 has been authorized")
    private Integer status;

    public InstitutionsForFateDto(FederatedFateManagerUserDo federatedFateManagerUserDo) {
        this.institutions = federatedFateManagerUserDo.getInstitutions();
    }
}
