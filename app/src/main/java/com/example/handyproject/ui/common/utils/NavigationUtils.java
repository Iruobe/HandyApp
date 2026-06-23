package com.example.handyproject.ui.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.handyproject.ui.customer.CustomerHomeActivity;
import com.example.handyproject.ui.handyman.HandymanHomeActivity;
import com.example.handyproject.utils.Constants;

public class NavigationUtils {

    public static void goHome(Activity activity) {
        String role = activity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(Constants.PREF_KEY_ROLE, Constants.ROLE_CUSTOMER);

        Class<?> target = Constants.ROLE_HANDYMAN.equals(role)
                ? HandymanHomeActivity.class
                : CustomerHomeActivity.class;

        activity.startActivity(new Intent(activity, target));
        activity.finish();
    }
}
