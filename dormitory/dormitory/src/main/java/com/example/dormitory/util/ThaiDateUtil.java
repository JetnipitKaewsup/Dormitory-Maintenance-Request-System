package com.example.dormitory.util;

import java.time.LocalDateTime;

public class ThaiDateUtil {

    private static final String[] THAI_MONTHS = {
            "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
            "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
    };

    private ThaiDateUtil() {
    }

    // เช่น "12 ก.พ. 2569"
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        int day = dateTime.getDayOfMonth();
        String month = THAI_MONTHS[dateTime.getMonthValue() - 1];
        int buddhistYear = dateTime.getYear() + 543;
        return day + " " + month + " " + buddhistYear;
    }

    // เช่น "12 ก.พ. 2569 09:00"
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return formatDate(dateTime) + " " +
                String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }
}