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
@ApiModel(value = "authority history qo")
public class AuthorityHistoryQo implements Serializable {

    @ApiModelProperty("page number")
    private Integer pageNum = 1;

    @ApiModelProperty("page size")
    private Integer pageSize = 10;

}
