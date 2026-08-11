package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Notification;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationRepository {

    public interface NotificationListCallback {
        void onUpdate(List<Notification> notifications);
        void onError(String message);
    }

    private ListenerRegistration listenerRegistration;

    public void startListening(NotificationListCallback callback) {
        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();
        if (user == null) {
            callback.onUpdate(Collections.emptyList());
            return;
        }

        listenerRegistration = FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_NOTIFICATIONS)
                .whereEqualTo(Constants.FIELD_USER_ID, user.getUid())
                .orderBy(Constants.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage() != null
                                ? error.getMessage() : "Failed to load notifications.");
                        return;
                    }
                    if (snapshots == null) return;

                    List<Notification> notifications = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Notification notification = doc.toObject(Notification.class);
                        notification.setId(doc.getId());
                        notifications.add(notification);
                    }
                    callback.onUpdate(notifications);
                });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    public void markAsRead(String notificationId) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_NOTIFICATIONS)
                .document(notificationId)
                .update(Constants.FIELD_READ, true);
    }
}
