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
@ApiModel(value = "institutions info for page")
public class InstitutionQo implements Serializable {

    @ApiModelProperty("page number")
    private Integer pageNum = 1;

    @ApiModelProperty("page size")
    private Integer pageSize = 10;

    public InstitutionQo(AuthorityInstitutionsQo authorityInstitutionsQo) {
        this.pageNum = authorityInstitutionsQo.getPageNum();
        this.pageSize = authorityInstitutionsQo.getPageSize();
    }
}
