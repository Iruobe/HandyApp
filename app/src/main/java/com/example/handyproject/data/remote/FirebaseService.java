package com.example.handyproject.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

public class FirebaseService {

    private static FirebaseAuth auth;
    private static FirebaseFirestore firestore;

    public static void initialize() {
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseFirestore getFirestore() {
        return firestore;
    }
}
