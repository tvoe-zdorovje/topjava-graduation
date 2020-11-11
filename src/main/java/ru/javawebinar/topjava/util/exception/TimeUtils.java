package ru.javawebinar.topjava.util.exception;

import java.time.Clock;
import java.time.LocalDateTime;

public class TimeUtils {
    private static final Clock CLOCK = Clock.systemDefaultZone();

    public static LocalDateTime now() {
        return LocalDateTime.now(CLOCK);
    }
}
