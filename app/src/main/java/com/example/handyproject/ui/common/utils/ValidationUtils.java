package com.example.handyproject.ui.common.utils;

import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;

public class ValidationUtils {

    public static boolean validateRequired(TextInputLayout til, String value, String fieldLabel) {
        if (value.isEmpty()) {
            til.setError(fieldLabel + " is required");
            return false;
        }
        til.setError(null);
        return true;
    }

    public static boolean validateEmail(TextInputLayout til, String email) {
        if (email.isEmpty()) {
            til.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            til.setError("Enter a valid email address");
            return false;
        }
        til.setError(null);
        return true;
    }

    public static boolean validatePassword(TextInputLayout til, String password) {
        if (password.isEmpty()) {
            til.setError("Password is required");
            return false;
        }
        if (password.length() < 8) {
            til.setError("Password must be at least 8 characters");
            return false;
        }
        til.setError(null);
        return true;
    }

    public static boolean validateConfirmPassword(TextInputLayout til,
                                                  String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            til.setError("Please confirm your password");
            return false;
        }
        if (!confirmPassword.equals(password)) {
            til.setError("Passwords do not match");
            return false;
        }
        til.setError(null);
        return true;
    }

    public static boolean validatePositiveDouble(TextInputLayout til, String value, String fieldLabel) {
        if (value.isEmpty()) {
            til.setError(fieldLabel + " is required");
            return false;
        }
        try {
            double parsed = Double.parseDouble(value);
            if (parsed <= 0) {
                til.setError(fieldLabel + " must be greater than £0");
                return false;
            }
        } catch (NumberFormatException e) {
            til.setError("Enter a valid " + fieldLabel.toLowerCase());
            return false;
        }
        til.setError(null);
        return true;
    }
}
