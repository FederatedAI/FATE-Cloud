package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "dto for typed jobs duration distribution")
public class JobTypedDurationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String duration;
    private Long count;

}
