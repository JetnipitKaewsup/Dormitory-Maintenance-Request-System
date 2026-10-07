package com.example.dormitory.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DateTimeUtil {

    private static final ZoneId THAILAND_ZONE =
            ZoneId.of("Asia/Bangkok");

    private DateTimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(THAILAND_ZONE);
    }
}
