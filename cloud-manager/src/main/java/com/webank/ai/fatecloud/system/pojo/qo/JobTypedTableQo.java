package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "qo for typed jobs table")
public class JobTypedTableQo extends FinishedJobsSummaryQo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;

}
