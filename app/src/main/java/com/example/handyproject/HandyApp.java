package com.example.handyproject;

import android.app.Application;
import com.example.handyproject.data.remote.FirebaseService;

public class HandyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseService.initialize();
    }
}
