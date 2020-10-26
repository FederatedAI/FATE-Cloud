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
@ApiModel(value = "institutions info")
public class FateManagerUserAddQo implements Serializable {

    @ApiModelProperty("institution name")
    private String institutions;

    @ApiModelProperty("creator name")
    private String creator;
}
