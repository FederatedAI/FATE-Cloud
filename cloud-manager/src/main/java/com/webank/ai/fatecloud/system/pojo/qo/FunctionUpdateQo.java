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
@ApiModel(value = "function to update")

public class FunctionUpdateQo implements Serializable {

    @ApiModelProperty(value = "primary key")
    private Long functionId;

    @ApiModelProperty(value = "function status")
    private Integer status;

}
