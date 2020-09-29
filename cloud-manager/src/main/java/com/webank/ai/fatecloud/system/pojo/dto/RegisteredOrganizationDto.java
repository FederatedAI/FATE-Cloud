package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "federated organization info")
public class RegisteredOrganizationDto implements Serializable {
    @ApiModelProperty(value = "organization info")
    public FederatedOrganizationDto federatedOrganizationDto;

    @ApiModelProperty(value = "total used count for group ")
    public Integer total;
}
