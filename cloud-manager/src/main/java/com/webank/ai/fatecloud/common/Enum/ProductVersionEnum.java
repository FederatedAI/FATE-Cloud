package com.webank.ai.fatecloud.common.Enum;

public enum ProductVersionEnum {
    FATE("FATE");

    private String name;

    ProductVersionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
