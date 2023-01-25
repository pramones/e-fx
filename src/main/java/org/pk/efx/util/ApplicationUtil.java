package org.pk.efx.util;

public class ApplicationUtil {

    public static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    public static String getRootPath() {
        return Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }
}
