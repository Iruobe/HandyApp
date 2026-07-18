package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.User;
import com.example.handyproject.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UserRepository {

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchUser(String uid, UserCallback callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) user.setUid(doc.getId());
                        callback.onSuccess(user);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to load account."));
    }

    public void createUser(String uid, Map<String, Object> data, SimpleCallback callback) {
        db.collection("users").document(uid).set(data)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to save user data."));
    }

    public void updateUser(String uid, Map<String, Object> updates, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_USERS).document(uid).update(updates)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to update profile."));
    }

    public void updateFcmToken(String uid, String token, SimpleCallback callback) {
        db.collection(Constants.COLLECTION_USERS).document(uid)
                .update(Constants.FIELD_FCM_TOKEN, token)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() != null ? e.getMessage() : "Failed to update device token."));
    }
}
