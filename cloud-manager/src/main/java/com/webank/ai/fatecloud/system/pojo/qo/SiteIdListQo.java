package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "site id list to query")
public class SiteIdListQo implements Serializable {

    @ApiModelProperty(value = "site id list to query")
    private List<Long> siteIdList;

}
