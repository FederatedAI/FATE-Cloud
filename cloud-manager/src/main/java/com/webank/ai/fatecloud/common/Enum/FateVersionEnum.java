package com.webank.ai.fatecloud.common.Enum;

public enum FateVersionEnum  {

    V_1_2_0("1.2.0"),
    V_1_2_1("1.2.1"),
    V_1_2_2("1.2.2"),
    V_1_3_0("1.3.0"),
    V_1_3_1("1.3.1"),
    V_1_4_0("1.4.0"),
    V_1_4_1("1.4.1"),
    V_1_4_2("1.4.2"),
    V_1_4_3("1.4.3"),
    V_1_4_4("1.4.4");

    public String getVersion() {
        return version;
    }

    FateVersionEnum(String version){
        this.version = version;
    }

    private String version;

}
