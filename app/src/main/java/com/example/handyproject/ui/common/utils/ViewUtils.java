package com.example.handyproject.ui.common.utils;

import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class ViewUtils {

    public static void fixNavOverlap(View scrollContent, View bottomNav) {
        bottomNav.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            int navH = v.getHeight();
            if (scrollContent.getPaddingBottom() != navH) {
                scrollContent.setPadding(
                    scrollContent.getPaddingLeft(),
                    scrollContent.getPaddingTop(),
                    scrollContent.getPaddingRight(),
                    navH
                );
            }
        });
    }

    public static String getTextFrom(TextInputLayout til) {
        return til.getEditText() != null
                ? til.getEditText().getText().toString().trim() : "";
    }

    public static void setLoading(ProgressBar progressBar, MaterialButton button, boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        button.setEnabled(!loading);
    }
}
