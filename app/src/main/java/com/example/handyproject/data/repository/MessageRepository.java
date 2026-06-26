package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Conversation;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageRepository {

    public interface ConversationListCallback {
        void onUpdate(List<Conversation> conversations);
        void onError(String message);
    }

    private ListenerRegistration listenerRegistration;

    public void startListening(ConversationListCallback callback) {
        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();
        if (user == null) {
            callback.onUpdate(Collections.emptyList());
            return;
        }

        listenerRegistration = FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .whereArrayContains(Constants.FIELD_PARTICIPANT_IDS, user.getUid())
                .orderBy(Constants.FIELD_LAST_MESSAGE_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage() != null
                                ? error.getMessage() : "Failed to load conversations.");
                        return;
                    }
                    if (snapshots == null) return;

                    List<Conversation> conversations = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Conversation conversation = doc.toObject(Conversation.class);
                        conversation.setId(doc.getId());
                        conversations.add(conversation);
                    }
                    callback.onUpdate(conversations);
                });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
