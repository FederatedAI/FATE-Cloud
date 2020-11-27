package com.webank.ai.fatecloud.common.util;

public class CheckVersion {
    public static boolean checkVersion(String version) {
        String regex = "^[0-9][0-9a-zA-Z\\.\\-]+[0-9a-zA-Z]$";

        if (version.matches(regex)) {
            return true;
        }
        return false;

    }
}
