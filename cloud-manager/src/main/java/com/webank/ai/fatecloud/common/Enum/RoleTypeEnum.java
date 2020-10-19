package com.webank.ai.fatecloud.common.Enum;

public enum RoleTypeEnum {

    GUEST(1, "Guest"),
    HOST(2, "Host");

    RoleTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
