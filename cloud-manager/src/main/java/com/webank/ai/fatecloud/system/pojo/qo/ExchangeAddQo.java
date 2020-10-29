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
@ApiModel(value = "exchange add qo")
public class ExchangeAddQo implements Serializable {

    @ApiModelProperty(value = "exchange name")
    private String exchangeName;

    @ApiModelProperty(value = "network access entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    private String networkAccessExits;


}
