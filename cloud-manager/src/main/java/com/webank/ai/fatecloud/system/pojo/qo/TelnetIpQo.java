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
@ApiModel("telnet ip port")
public class TelnetIpQo implements Serializable {

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "port")
    private Integer port;
}
