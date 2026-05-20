package com.example.handyproject.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("d MMM yyyy", Locale.UK);

    private static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.UK);

    public static String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATE_FORMAT.format(timestamp.toDate());
    }

    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATE_TIME_FORMAT.format(timestamp.toDate());
    }
}
