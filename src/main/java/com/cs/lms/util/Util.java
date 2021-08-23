package com.cs.lms.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {
    /**
     * Utility method to generate ISBN' sequence for demo purpose,
     * in real word it should be the value provided on book.
     */
    private static final AtomicInteger LMS_USER_SEQUENCE_GENERATOR = new AtomicInteger(10000);
    public static String generateLmsUserId(){
        return "LMS-" + LMS_USER_SEQUENCE_GENERATOR.getAndIncrement();
    }

    public static String currentLmsUserId(){
        return "LMS-" + (LMS_USER_SEQUENCE_GENERATOR.get() -1 );
    }

    public static boolean toBoolean(String exact) {
        if(Objects.nonNull(exact)){
            return Boolean.valueOf(exact);
        }
        return false;
    }
}
