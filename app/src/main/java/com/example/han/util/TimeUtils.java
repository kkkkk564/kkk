package com.example.han.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    private static final SimpleDateFormat INPUT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat OUTPUT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static String formatTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        try {
            Date date = INPUT_FORMAT.parse(timeStr);
            if (date != null) return OUTPUT_FORMAT.format(date);
        } catch (Exception e) {
            return timeStr;
        }
        return timeStr;
    }

    public static String formatRelativeTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        try {
            Date date = INPUT_FORMAT.parse(timeStr);
            if (date == null) return timeStr;
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            if (seconds < 60) return "刚刚";
            long minutes = seconds / 60;
            if (minutes < 60) return minutes + "分钟前";
            long hours = minutes / 60;
            if (hours < 24) return hours + "小时前";
            long days = hours / 24;
            if (days < 30) return days + "天前";
            return OUTPUT_FORMAT.format(date);
        } catch (Exception e) {
            return timeStr;
        }
    }
}
