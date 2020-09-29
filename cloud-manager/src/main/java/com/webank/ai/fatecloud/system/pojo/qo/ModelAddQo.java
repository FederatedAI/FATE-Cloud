package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("model bean for add")
public class ModelAddQo implements Serializable {
    @ApiModelProperty(value = "model name")
    private String installItems;

    @ApiModelProperty(value = "model version")
    private String version;

    @ApiModelProperty(value = "mode update status")
    private Integer updateStatus;

    @ApiModelProperty(value = "system type")
    private String type;

    @ApiModelProperty(value = "site id")
    private Long id;

    @ApiModelProperty(value = "update time")
    private Date updateTime;

}
