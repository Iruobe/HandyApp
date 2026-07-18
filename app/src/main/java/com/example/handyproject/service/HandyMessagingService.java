package com.example.handyproject.service;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.customer.ChatThreadActivity;
import com.example.handyproject.ui.customer.MessagesActivity;
import com.example.handyproject.utils.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class HandyMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();
        if (user == null) return;

        new UserRepository().updateFcmToken(user.getUid(), token, new UserRepository.SimpleCallback() {
            @Override public void onSuccess() {}
            @Override public void onFailure(String message) {}
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle()
                : remoteMessage.getData().get("title");
        String body = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody()
                : remoteMessage.getData().get("body");
        String conversationId = remoteMessage.getData().get("conversationId");

        showNotification(
                title != null ? title : "Handy",
                body != null ? body : "You have a new notification",
                conversationId);
    }

    private void showNotification(String title, String body, String conversationId) {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return;

        Intent intent;
        if (conversationId != null) {
            intent = new Intent(this, ChatThreadActivity.class);
            intent.putExtra(ChatThreadActivity.EXTRA_CONVERSATION_ID, conversationId);
        } else {
            intent = new Intent(this, MessagesActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }
}
