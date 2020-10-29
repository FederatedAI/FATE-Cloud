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
@ApiModel(value = "exchange delete qo")
public class ExchangeDeleteQo implements Serializable {

    @ApiModelProperty(value = "exchange id")
    private Long exchangeId;

}
