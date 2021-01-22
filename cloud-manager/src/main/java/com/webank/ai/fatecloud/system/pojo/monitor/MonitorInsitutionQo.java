package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MonitorInsitutionQo implements Serializable {
    @ApiModelProperty("monitorReq")
    private MonitorReq  monitorReq;

    @ApiModelProperty("institution")
    private String  institution;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;
}