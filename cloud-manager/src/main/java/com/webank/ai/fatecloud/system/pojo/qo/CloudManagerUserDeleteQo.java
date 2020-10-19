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
@ApiModel(value = "cloud manager user id")
public class CloudManagerUserDeleteQo implements Serializable {

    @ApiModelProperty("cloud manager user id")
    private Long cloudManagerId;
}
