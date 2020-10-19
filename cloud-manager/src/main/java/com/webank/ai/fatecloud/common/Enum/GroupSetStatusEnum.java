package com.webank.ai.fatecloud.common.Enum;

public enum GroupSetStatusEnum  {

    VALID(1, "Valid"),
    DELETE(2, "Delete");

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    GroupSetStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

}
