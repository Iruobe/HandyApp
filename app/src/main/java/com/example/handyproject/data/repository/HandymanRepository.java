package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Handyman;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HandymanRepository {

    public interface HandymanListCallback {
        void onUpdate(List<Handyman> handymen);
        void onError(String message);
    }

    private ListenerRegistration listenerRegistration;

    public void startListening(HandymanListCallback callback) {
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("role", "handyman")
                .limit(10)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage() != null
                                ? error.getMessage() : "Failed to load handymen.");
                        return;
                    }
                    if (snapshots == null) return;

                    List<Handyman> handymen = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Handyman h = doc.toObject(Handyman.class);
                        h.setUid(doc.getId());
                        handymen.add(h);
                    }
                    callback.onUpdate(handymen);
                });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
