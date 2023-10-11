package com.zhd.shj;

import java.lang.reflect.Method;

public class SystemPropertiesHelper {

    public static String getSystemProperty(String key) {
        String value = "";

        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = systemProperties.getMethod("get", String.class);
            value = (String) getMethod.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}
