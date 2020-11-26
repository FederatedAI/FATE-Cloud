package com.webank.ai.fatecloud.common.util;

public class CheckVersion {
    public static boolean checkVersion(String version) {
        String regex = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]+$";

        if (!version.matches(regex)) {
            return false;
        }

        return true;
    }
}
