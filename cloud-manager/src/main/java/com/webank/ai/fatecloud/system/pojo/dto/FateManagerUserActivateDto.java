package com.webank.ai.fatecloud.system.pojo.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "fate manager activate dto")
public class FateManagerUserActivateDto implements Serializable {

    @ApiModelProperty(value = "primary key")
    private String fateManagerId;

    @ApiModelProperty(value = "institution")
    private String institution;

}
