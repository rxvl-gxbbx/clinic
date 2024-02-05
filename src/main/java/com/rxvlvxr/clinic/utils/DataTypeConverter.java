package com.rxvlvxr.clinic.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// нужен для конвертации xs:date и xs:dateTime в соответствующие им классы в Java
public class DataTypeConverter {

    public static LocalDate parseDate(String inputDate) {
        return inputDate != null ? DateTimeFormatter.ISO_DATE.parse(inputDate, LocalDate::from) : null;
    }

    public static String printDate(LocalDate inputDate) {
        return inputDate != null ? DateTimeFormatter.ISO_DATE.format(inputDate) : null;
    }

    public static LocalDateTime parseDateTime(String inputDate) {
        return inputDate != null ? LocalDateTime.parse(inputDate) : null;
    }

    public static String printDateTime(LocalDateTime inputDate) {
        return inputDate != null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(inputDate) : null;
    }
}