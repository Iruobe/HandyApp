package com.example.handyproject.ui.common.utils;

import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class ViewUtils {

    public static String getTextFrom(TextInputLayout til) {
        return til.getEditText() != null
                ? til.getEditText().getText().toString().trim() : "";
    }

    public static void setLoading(ProgressBar progressBar, MaterialButton button, boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        button.setEnabled(!loading);
    }
}
