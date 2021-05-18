package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "qo for finished jobs summary")
public class FinishedJobsSummaryQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date beginDate;

    private Date endDate;

    private String institutions;

    private String site;
}
