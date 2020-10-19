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
@ApiModel(value = "fate manager user to delete")
public class FateManagerUserDeleteQo implements Serializable {

    @ApiModelProperty("fate manager id")
    private String fateManagerId;

}
