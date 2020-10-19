package com.webank.ai.fatecloud.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel("object content")
public class IdNamePair implements Serializable {

    public IdNamePair() {
    }

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("code")
    private Object code;

    @ApiModelProperty("desc")
    private String desc;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
