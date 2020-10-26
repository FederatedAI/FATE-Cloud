package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.system.dao.entity.FederatedFunctionDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "all function status information")
public class FunctionStatusDto implements Serializable {

    public FunctionStatusDto(FederatedFunctionDo federatedFunctionDo) {
        this.functionId = federatedFunctionDo.getFunctionId();
        this.functionName = federatedFunctionDo.getFunctionName();
        this.status = federatedFunctionDo.getStatus();
    }

    @ApiModelProperty(value = "primary key")
    private Long functionId;

    @ApiModelProperty(value = "function name")
    private String functionName;

    @ApiModelProperty(value = "function status")
    private Integer status;
}
