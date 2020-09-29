package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "one site qo for fate manager")
public class OneSiteQo implements Serializable {
    public Long id;
}
