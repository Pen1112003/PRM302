package com.example.prm392_cinema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {

    public static String formatDateTime(String inputDateTime) {
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS", // Định dạng có mili giây (3 chữ số)
                "yyyy-MM-dd'T'HH:mm:ss.SS",  // Định dạng có mili giây (2 chữ số)
                "yyyy-MM-dd'T'HH:mm:ss.S",    // Định dạng có mili giây (1 chữ số)
                "yyyy-MM-dd'T'HH:mm:ss"        // Định dạng không có mili giây
        };

        // Định dạng đầu ra
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

        for (String format : formats) {
            try {
                // Chuyển đổi chuỗi thành LocalDateTime
                LocalDateTime dateTime = LocalDateTime.parse(inputDateTime, DateTimeFormatter.ofPattern(format));
                // Định dạng lại thành chuỗi đầu ra
                return dateTime.format(outputFormatter);
            } catch (DateTimeParseException e) {
                // Nếu xảy ra lỗi, thử định dạng tiếp theo
            }
        }
        return inputDateTime;
    }
}