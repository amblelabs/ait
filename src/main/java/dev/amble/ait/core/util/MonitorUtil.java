package dev.amble.ait.core.util;

public class MonitorUtil {
    public static String truncateDimensionName(String name, int maxLength) {
        if (name.length() > maxLength) {
            return name.substring(0, maxLength) + "...";
        }
        return name;
    }
}
