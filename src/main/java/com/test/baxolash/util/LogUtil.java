package com.test.baxolash.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtil {

    public static void info(String message, Object... args) {
        log.info(message, args);
    }

    public static void warn(String message, Object... args) {
        log.warn(message, args);
    }

    public static void error(String message, Object... args) {
        log.error(message, args);
    }
}

