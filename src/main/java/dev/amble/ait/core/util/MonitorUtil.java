package dev.amble.ait.core.util;

public class MonitorUtil {

    static float time = 0.0f;

    public static String truncateDimensionName(String name, int maxLength) {
        if (name.length() > maxLength) {
            return name.substring(0, maxLength) + "...";
        }
        return name;
    }

    // Use this one instead, it scrolls! - Loqor
    public static String scrollText(String name, int maxLength, int scrollIndex, float tickDelta) {
        time += tickDelta;
        if (name.length() <= maxLength) {
            return name;
        }
        int scrollRange = name.length() - maxLength;
        if (scrollRange <= 0) {
            return name;
        }
        int start = getStart(scrollIndex, scrollRange);
        String result = name.substring(start, start + maxLength);
        boolean atEnd = start == scrollRange;
        if (!atEnd) {
            result += "...";
        }
        return result;
    }

    private static int getStart(int scrollIndex, int scrollRange) {
        int pauseTicks = 15; // Number of ticks to pause at ends (increase for longer pause)
        float effectiveScroll = scrollIndex + (time * 0.02f);
        int period = 2 * (scrollRange + pauseTicks);
        int pos = ((int) effectiveScroll) % period;
        int start;
        if (pos < scrollRange) {
            start = pos;
        } else if (pos < scrollRange + pauseTicks) {
            start = scrollRange;
        } else if (pos < 2 * scrollRange + pauseTicks) {
            start = scrollRange - (pos - (scrollRange + pauseTicks));
        } else {
            start = 0;
        }
        return start;
    }
}
