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
@ApiModel(value = "page info")
public class PageInfoQo implements Serializable {
    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "role, 0:unknow, 1:guest, 2:host")
    private Integer role;

}
