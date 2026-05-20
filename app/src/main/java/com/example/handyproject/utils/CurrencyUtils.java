package com.example.handyproject.utils;

import java.util.Locale;

public class CurrencyUtils {

    public static String formatRate(double hourlyRate) {
        return String.format(Locale.UK, "£%.2f/hr", hourlyRate);
    }

    public static String formatAmount(double amount) {
        return String.format(Locale.UK, "£%.2f", amount);
    }
}
