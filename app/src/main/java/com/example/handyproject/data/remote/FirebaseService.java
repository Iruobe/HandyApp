package com.example.handyproject.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseService {

    private static FirebaseAuth auth;
    private static FirebaseFirestore firestore;
    private static FirebaseStorage storage;

    public static void initialize() {
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);

        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    public static FirebaseStorage getStorage() {
        return storage;
    }
}
