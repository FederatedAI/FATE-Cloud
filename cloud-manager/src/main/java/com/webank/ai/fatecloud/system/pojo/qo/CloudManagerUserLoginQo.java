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
@ApiModel(value = "cloud manager user login info")

public class CloudManagerUserLoginQo implements Serializable {
    @ApiModelProperty("cloud manager user name")
    private String name;

    @ApiModelProperty(value = "user password")
    private String password;
}
