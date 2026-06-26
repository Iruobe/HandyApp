package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Booking;
import com.example.handyproject.data.model.Conversation;
import com.example.handyproject.data.model.Message;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageRepository {

    public interface ConversationListCallback {
        void onUpdate(List<Conversation> conversations);
        void onError(String message);
    }

    public interface ThreadMessageCallback {
        void onUpdate(List<Message> messages);
        void onError(String message);
    }

    public interface MessageSendCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface FindOrCreateConversationCallback {
        void onSuccess(String conversationId);
        void onError(String message);
    }

    private static final int THREAD_MESSAGE_LIMIT = 30;

    private ListenerRegistration listenerRegistration;
    private ListenerRegistration threadListenerRegistration;

    // ── Conversation list ─────────────────────────────────────────────────────

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

    // ── Chat thread ───────────────────────────────────────────────────────────

    public void startThreadListening(String conversationId, ThreadMessageCallback callback) {
        // TODO: load-more beyond 30 messages is deferred to a future round
        threadListenerRegistration = FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .collection(Constants.COLLECTION_MESSAGES)
                .orderBy(Constants.FIELD_SENT_AT, Query.Direction.ASCENDING)
                .limit(THREAD_MESSAGE_LIMIT)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error.getMessage() != null
                                ? error.getMessage() : "Failed to load messages.");
                        return;
                    }
                    if (snapshots == null) return;

                    List<Message> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Message message = doc.toObject(Message.class);
                        message.setMessageId(doc.getId());
                        messages.add(message);
                    }
                    callback.onUpdate(messages);
                });
    }

    public void stopThreadListening() {
        if (threadListenerRegistration != null) {
            threadListenerRegistration.remove();
            threadListenerRegistration = null;
        }
    }

    public void sendMessage(String conversationId, String text, MessageSendCallback callback) {
        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();
        if (user == null) {
            callback.onError("Not signed in.");
            return;
        }

        Timestamp now = Timestamp.now();

        Message message = new Message();
        message.setSenderId(user.getUid());
        message.setType(Constants.TYPE_TEXT);
        message.setBody(text);
        message.setSentAt(now);

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .collection(Constants.COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener(ref -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put(Constants.FIELD_LAST_MESSAGE, text);
                    update.put(Constants.FIELD_LAST_MESSAGE_TIMESTAMP, now);

                    FirebaseService.getFirestore()
                            .collection(Constants.COLLECTION_CONVERSATIONS)
                            .document(conversationId)
                            .update(update)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(
                                    e.getMessage() != null ? e.getMessage()
                                            : "Failed to update conversation."));
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to send message."));
    }

    // ── Conversation find-or-create ───────────────────────────────────────────

    public void findOrCreateConversation(String customerUid, String customerName,
                                         String handymanUid, String handymanName,
                                         FindOrCreateConversationCallback callback) {
        String[] uids = {customerUid, handymanUid};
        Arrays.sort(uids);
        String conversationId = uids[0] + "_" + uids[1];

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(conversationId);
                    } else {
                        List<String> participantIds = new ArrayList<>();
                        participantIds.add(customerUid);
                        participantIds.add(handymanUid);

                        Map<String, String> participantNames = new HashMap<>();
                        participantNames.put(customerUid,
                                (customerName == null || customerName.isEmpty())
                                        ? "Customer" : customerName);
                        participantNames.put(handymanUid,
                                (handymanName == null || handymanName.isEmpty())
                                        ? "Handyman" : handymanName);

                        Map<String, Object> data = new HashMap<>();
                        data.put(Constants.FIELD_PARTICIPANT_IDS, participantIds);
                        data.put(Constants.FIELD_PARTICIPANT_NAMES, participantNames);

                        FirebaseService.getFirestore()
                                .collection(Constants.COLLECTION_CONVERSATIONS)
                                .document(conversationId)
                                .set(data)
                                .addOnSuccessListener(unused -> callback.onSuccess(conversationId))
                                .addOnFailureListener(e -> callback.onError(
                                        e.getMessage() != null ? e.getMessage()
                                                : "Failed to create conversation."));
                    }
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage()
                                : "Failed to find conversation."));
    }

    // ── Booking message ───────────────────────────────────────────────────────

    public void postBookingMessage(String conversationId, Booking booking,
                                   MessageSendCallback callback) {
        Timestamp now = Timestamp.now();

        Message message = new Message();
        message.setSenderId(booking.getCustomerId());
        message.setType(Constants.TYPE_BOOKING);
        message.setBody("Booking request");
        message.setSentAt(now);
        message.setBookingScheduledAt(booking.getScheduledAt());
        message.setBookingAddress(booking.getAddress());
        message.setBookingNotes(booking.getNotes());

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .collection(Constants.COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener(ref -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put(Constants.FIELD_LAST_MESSAGE, "Booking request");
                    update.put(Constants.FIELD_LAST_MESSAGE_TIMESTAMP, now);

                    FirebaseService.getFirestore()
                            .collection(Constants.COLLECTION_CONVERSATIONS)
                            .document(conversationId)
                            .update(update)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(
                                    e.getMessage() != null ? e.getMessage()
                                            : "Failed to update conversation."));
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage()
                                : "Failed to post booking message."));
    }
}
