package com.webank.ai.fatecloud.common.util;

public class ObjectUtil {

    public static boolean isEmpty(Object... args) {
        if (args == null) return true;
        for (Object o : args) {
            if (o == null || o.toString().trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public static boolean noEmpty(Object... args) {
        return !isEmpty(args);
    }

    public static boolean equals(Object object, Object target) {
        if (object != null && target != null) {
            return object == target || object.equals(target);
        }

        return false;
    }

    public static boolean matchNetworkAddress(String address) {
        if (address == null) return false;
        return address.matches("[\\d]{1,3}(\\.[\\d]{1,3}){3}:[0-9]{1,5}");
    }
}
