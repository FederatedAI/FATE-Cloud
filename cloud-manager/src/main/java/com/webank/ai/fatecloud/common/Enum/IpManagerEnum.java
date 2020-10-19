package com.webank.ai.fatecloud.common.Enum;

public enum IpManagerEnum implements InterfaceEnum{


    UNKONWN(-1, "Unknown"),
    NO_DEAL(0,"No Deal"),
    AGREED(1, "Agreed"),
    REJECTED(2, "Rejected");

    IpManagerEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public static IpManagerEnum getEnumByType(Integer codeVaule){
        if (null == codeVaule){
            return UNKONWN;
        }
        for (IpManagerEnum ele: IpManagerEnum.values()) {
            if (ele.getValue().equals(codeVaule)){
                return ele;
            }
        }
        return UNKONWN;
    }
    @Override
    public Integer getValue() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
