package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Review;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReviewRepository {

    public interface ReviewListCallback {
        void onSuccess(List<Review> reviews);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface SingleReviewCallback {
        void onSuccess(Review review);
        void onError(String message);
    }

    public interface EarnedGateCallback {
        void onResult(boolean hasConfirmedBooking);
        void onError(String message);
    }

    public void getReviewsForHandyman(String handymanId, ReviewListCallback callback) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_REVIEWS)
                .whereEqualTo(Constants.FIELD_HANDYMAN_ID, handymanId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Review review = doc.toObject(Review.class);
                        review.setId(doc.getId());
                        reviews.add(review);
                    }
                    // Sort by createdAt DESC client-side — avoids composite index on (handymanId, createdAt)
                    Collections.sort(reviews, (a, b) -> {
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    });
                    callback.onSuccess(reviews);
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to load reviews."));
    }

    public void submitReview(Review review, SimpleCallback callback) {
        review.setCreatedAt(Timestamp.now());
        String docId = review.getHandymanId() + "_" + review.getCustomerId();

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_REVIEWS)
                .document(docId)
                .set(review)
                .addOnSuccessListener(unused ->
                        recomputeHandymanRating(review.getHandymanId(), callback))
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to submit review."));
    }

    private void recomputeHandymanRating(String handymanId, SimpleCallback callback) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_REVIEWS)
                .whereEqualTo(Constants.FIELD_HANDYMAN_ID, handymanId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        callback.onSuccess();
                        return;
                    }
                    double sum = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Long r = doc.getLong(Constants.FIELD_RATING);
                        if (r != null) sum += r;
                    }
                    double average = sum / snapshots.size();

                    FirebaseService.getFirestore()
                            .collection(Constants.COLLECTION_USERS)
                            .document(handymanId)
                            .update(Constants.FIELD_RATING, average)
                            .addOnSuccessListener(v -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onSuccess()); // review written; rating sync is non-critical
                })
                .addOnFailureListener(e -> callback.onSuccess()); // review written; rating sync is non-critical
    }

    public void getExistingReview(String handymanId, String customerId, SingleReviewCallback callback) {
        String docId = handymanId + "_" + customerId;
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_REVIEWS)
                .document(docId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onSuccess(null);
                        return;
                    }
                    Review review = doc.toObject(Review.class);
                    if (review != null) review.setId(doc.getId());
                    callback.onSuccess(review);
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to fetch existing review."));
    }

    public void hasConfirmedBooking(String customerId, String handymanId, EarnedGateCallback callback) {
        String[] uids = {customerId, handymanId};
        Arrays.sort(uids);
        String conversationId = uids[0] + "_" + uids[1];

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_CONVERSATIONS)
                .document(conversationId)
                .collection(Constants.COLLECTION_MESSAGES)
                .whereEqualTo(Constants.FIELD_TYPE, Constants.TYPE_BOOKING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        if (Constants.BOOKING_STATUS_CONFIRMED.equals(
                                doc.getString(Constants.FIELD_BOOKING_STATUS))) {
                            callback.onResult(true);
                            return;
                        }
                    }
                    callback.onResult(false);
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to check booking status."));
    }
}
