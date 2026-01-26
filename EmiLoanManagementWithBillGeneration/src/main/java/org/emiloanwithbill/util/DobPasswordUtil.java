package org.emiloanwithbill.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DobPasswordUtil {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("ddMMyyyy");

    public static String fromDob(LocalDate dob) {
        return dob.format(FORMATTER);
    }
}
