package com.webank.ai.fatecloud.common.util;

public class CheckInstitutionsUtil {
    public static boolean checkPath(String... paths){
        String regex = "^[0-9a-zA-Z\\-_\\u4e00-\\u9fa5\\s]+$";
        for (String parameter : paths) {
            if (!parameter.matches(regex)) {
                return false;
            }
        }
        return true;
    }
}
