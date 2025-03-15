package dev.amble.ait.core.util;

public class TimeUtil {

    public static int secondsToMilliseconds(int seconds) {
        return seconds * 1000;
    }

    public static int minutesToMilliseconds(int minutes) {
        return secondsToMilliseconds(minutes * 60);
    }

    public static int hoursToMilliseconds(int hours) {
        return minutesToMilliseconds(hours * 60);
    }

    public static int millisecondsToSeconds(int milliseconds) {
        return milliseconds / 1000;
    }

    public static int millisecondsToMinutes(int milliseconds) {
        return millisecondsToSeconds(milliseconds) / 60;
    }

    public static int millisecondsToHours(int milliseconds) {
        return millisecondsToMinutes(milliseconds) / 60;
    }
}
