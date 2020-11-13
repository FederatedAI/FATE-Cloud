package com.webank.ai.fatecloud.system.pojo.qo;

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
@ApiModel("model status to update")
public class ModelHeartQo implements Serializable {
    @ApiModelProperty(value = "model name")
    private String installItems;

    @ApiModelProperty(value = "model version")
    private String version;

    @ApiModelProperty(value = "site id")
    private Long id;

    @ApiModelProperty(value = "model detective status")
    private Integer detectiveStatus;

}
