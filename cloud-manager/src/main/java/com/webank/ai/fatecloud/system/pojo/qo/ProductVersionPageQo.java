package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionPageQo implements Serializable {

    private String productVersion;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
