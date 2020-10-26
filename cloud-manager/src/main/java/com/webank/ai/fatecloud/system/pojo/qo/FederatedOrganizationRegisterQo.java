package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("Register Federaton")
public class FederatedOrganizationRegisterQo implements Serializable {

    @ApiModelProperty(value = "Federation Name")
    public String name;

    @ApiModelProperty(value = "Federation Institution")
    public String institution;

}
