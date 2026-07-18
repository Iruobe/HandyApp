package com.example.handyproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;

public class HandyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseService.initialize();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Handy Notifications",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Messages, bookings, and updates");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
