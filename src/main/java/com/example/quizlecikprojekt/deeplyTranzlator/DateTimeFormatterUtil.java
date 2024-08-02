package com.example.quizlecikprojekt.deeplyTranzlator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}