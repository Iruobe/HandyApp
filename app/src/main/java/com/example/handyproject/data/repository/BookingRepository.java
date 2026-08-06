package com.example.handyproject.data.repository;

import com.example.handyproject.data.model.Booking;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.utils.Constants;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Queryable source of truth for booking stats (Total Jobs, Recent Enquiries).
 * Parallel to the booking-type chat messages — never a replacement for them.
 * Doc ID = the booking message's Firestore document ID (see createBooking), so
 * the confirm/deny flow can sync a record using only the messageId it already has.
 * Mirrors ReviewRepository: single-field handymanId query + client-side count/sort,
 * so no composite index is required.
 */
public class BookingRepository {

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface CountCallback {
        void onSuccess(int count);
        void onError(String message);
    }

    public interface BookingListCallback {
        void onSuccess(List<Booking> bookings);
        void onError(String message);
    }

    private static final int RECENT_LIMIT = 3;

    // ── Dual-write on booking create ──────────────────────────────────────────

    /** messageId = the booking message's doc ID; used as this record's doc ID. */
    public void createBooking(String messageId, Booking booking, SimpleCallback callback) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_BOOKINGS)
                .document(messageId)
                .set(booking)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to create booking record."));
    }

    // ── Status sync on confirm/deny ───────────────────────────────────────────

    /**
     * Updates the record keyed by the booking message's ID. Uses update() (not
     * set-merge), so a pre-this-round booking with no record is a silent no-op —
     * the "fresh start, no backfill" behaviour.
     */
    public void updateBookingStatus(String messageId, String status, Double quoteAmount,
                                    SimpleCallback callback) {
        Map<String, Object> update = new HashMap<>();
        update.put(Constants.FIELD_STATUS, status);
        if (quoteAmount != null) {
            update.put(Constants.FIELD_QUOTE_AMOUNT, quoteAmount);
        }

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_BOOKINGS)
                .document(messageId)
                .update(update)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to update booking record."));
    }

    // ── Home-screen reads ─────────────────────────────────────────────────────

    /** Total Jobs = this handyman's bookings with status "confirmed". */
    public void countConfirmedForHandyman(String handymanId, CountCallback callback) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_BOOKINGS)
                .whereEqualTo(Constants.FIELD_HANDYMAN_ID, handymanId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    int count = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        if (Constants.BOOKING_STATUS_CONFIRMED.equals(
                                doc.getString(Constants.FIELD_STATUS))) {
                            count++;
                        }
                    }
                    callback.onSuccess(count);
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to count bookings."));
    }

    /** Recent Enquiries = this handyman's last 3 bookings (any status), newest first. */
    public void getRecentForHandyman(String handymanId, BookingListCallback callback) {
        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_BOOKINGS)
                .whereEqualTo(Constants.FIELD_HANDYMAN_ID, handymanId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Booking booking = doc.toObject(Booking.class);
                        booking.setBookingId(doc.getId());
                        bookings.add(booking);
                    }
                    // Sort createdAt DESC client-side — avoids a composite index on
                    // (handymanId, createdAt); mirrors ReviewRepository.
                    Collections.sort(bookings, (a, b) -> {
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    });
                    if (bookings.size() > RECENT_LIMIT) {
                        bookings = new ArrayList<>(bookings.subList(0, RECENT_LIMIT));
                    }
                    callback.onSuccess(bookings);
                })
                .addOnFailureListener(e -> callback.onError(
                        e.getMessage() != null ? e.getMessage() : "Failed to load recent bookings."));
    }
}
