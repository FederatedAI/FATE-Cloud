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
@ApiModel(value = "cloud manager paged user info")

public class CloudManagerUserPagedQo implements Serializable {
    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("cloud manager user name")
    private String name;


}
