package com.webank.ai.fatecloud.common.Enum;

import java.io.Serializable;

public enum SiteStatusEnum    {

    NOT_JOIN(1, "Not Join"),
    JOINED(2, "Joined"),
    REMOVED(3,"Removed");

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    SiteStatusEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

}
