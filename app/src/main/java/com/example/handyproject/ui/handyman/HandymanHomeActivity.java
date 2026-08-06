package com.example.handyproject.ui.handyman;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Booking;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.BookingRepository;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.adapters.EnquiryAdapter;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.ui.customer.MessagesActivity;
import com.example.handyproject.ui.customer.NotificationsActivity;
import com.example.handyproject.ui.customer.ProfileActivity;
import com.example.handyproject.ui.customer.SearchActivity;
import com.example.handyproject.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HandymanHomeActivity extends AppCompatActivity {

    public static class EnquiryItem {
        public String customerName;
        public String message;
        public String timestamp;
        public String status;

        public EnquiryItem(String customerName, String message,
                           String timestamp, String status) {
            this.customerName = customerName;
            this.message      = message;
            this.timestamp    = timestamp;
            this.status       = status;
        }
    }

    private final HandymanRepository handymanRepository = new HandymanRepository();
    private final BookingRepository  bookingRepository  = new BookingRepository();
    private String currentUid = "";

    private TextView tvRating;
    private TextView tvTotalJobs;
    private TextView tvNewBadge;
    private RecyclerView rvEnquiries;
    private EnquiryAdapter enquiryAdapter;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private final List<EnquiryItem> enquiries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_home);
        ViewUtils.fixNavOverlap(findViewById(R.id.scrollContent), findViewById(R.id.bottomNav));

        tvRating    = findViewById(R.id.tvRating);
        tvTotalJobs = findViewById(R.id.tvTotalJobs);
        tvNewBadge  = findViewById(R.id.tvNewBadge);
        rvEnquiries = findViewById(R.id.rvEnquiries);

        FirebaseUser firebaseUser = new AuthRepository().getCurrentUser();
        currentUid = firebaseUser != null ? firebaseUser.getUid() : "";

        loadRating();
        setupEnquiries();
        setupBadge();
        setupBottomNav();
        requestNotificationPermission();

        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        ImageView ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ImageUtils.loadAvatar(ivUserAvatar, null);
        ivUserAvatar.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.btnViewAllEnquiries).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh on return (e.g. after a booking is confirmed elsewhere).
        loadBookingStats();
    }

    /** Mirrors HandymanProfileActivity's header: real synced handyman.rating, same formatting. */
    private void loadRating() {
        if (currentUid.isEmpty()) return;

        handymanRepository.fetchHandyman(currentUid, new HandymanRepository.HandymanCallback() {
            @Override
            public void onSuccess(Handyman handyman) {
                if (handyman == null) return;
                tvRating.setText(handyman.getRating() > 0
                        ? String.format(Locale.UK, "%.1f", handyman.getRating())
                        : "Not rated");
            }

            @Override
            public void onError(String message) {
                /* silent — keeps the "Not rated" placeholder from the layout */
            }
        });
    }

    private void setupEnquiries() {
        // Data comes from the bookings collection in loadBookingStats(); starts empty.
        rvEnquiries.setLayoutManager(new LinearLayoutManager(this));
        enquiryAdapter = new EnquiryAdapter(enquiries);
        rvEnquiries.setAdapter(enquiryAdapter);
        rvEnquiries.setNestedScrollingEnabled(false);
    }

    /** Total Jobs = confirmed bookings; Recent Enquiries = last 3 bookings (any status). */
    private void loadBookingStats() {
        if (currentUid.isEmpty()) return;

        bookingRepository.countConfirmedForHandyman(currentUid,
                new BookingRepository.CountCallback() {
                    @Override
                    public void onSuccess(int count) {
                        tvTotalJobs.setText(String.valueOf(count));
                    }

                    @Override
                    public void onError(String message) {
                        /* silent — keeps the "0" placeholder */
                    }
                });

        bookingRepository.getRecentForHandyman(currentUid,
                new BookingRepository.BookingListCallback() {
                    @Override
                    public void onSuccess(List<Booking> bookings) {
                        enquiries.clear();
                        int pending = 0;
                        for (Booking booking : bookings) {
                            enquiries.add(toEnquiryItem(booking));
                            if (Constants.BOOKING_STATUS_PENDING.equals(booking.getStatus())) {
                                pending++;
                            }
                        }
                        enquiryAdapter.notifyDataSetChanged();

                        if (pending > 0) {
                            tvNewBadge.setText(pending + " New");
                            tvNewBadge.setVisibility(View.VISIBLE);
                        } else {
                            tvNewBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        /* silent — leaves the current list/badge as-is */
                    }
                });
    }

    private EnquiryItem toEnquiryItem(Booking booking) {
        String name = (booking.getCustomerName() != null && !booking.getCustomerName().isEmpty())
                ? booking.getCustomerName() : "Customer";

        String message;
        if (booking.getNotes() != null && !booking.getNotes().trim().isEmpty()) {
            message = booking.getNotes();
        } else {
            message = booking.getAddress() != null ? booking.getAddress() : "";
        }

        return new EnquiryItem(name, message,
                formatRelativeTime(booking.getCreatedAt()),
                mapStatusToBadge(booking.getStatus()));
    }

    /** Maps a booking status to the three badge buckets EnquiryAdapter styles. */
    private String mapStatusToBadge(String status) {
        if (Constants.BOOKING_STATUS_CONFIRMED.equals(status)) return "Booked";   // teal
        if (Constants.BOOKING_STATUS_DENIED.equals(status))    return "Declined"; // grey
        return "New";                                                             // pending/null → red
    }

    // Relative-time formatting, mirroring NotificationAdapter's local helper.
    private String formatRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "";
        long now  = System.currentTimeMillis();
        long time = timestamp.toDate().getTime();
        long diff = now - time;

        if (diff < 60_000L) return "Just now";
        if (diff < 3_600_000L) {
            long minutes = diff / 60_000L;
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        if (diff < 86_400_000L) {
            long hours = diff / 3_600_000L;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        if (diff < 172_800_000L) return "Yesterday";
        return new SimpleDateFormat("d MMM", Locale.UK).format(timestamp.toDate());
    }

    private void setupBadge() {
        GradientDrawable badge = new GradientDrawable();
        badge.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        badge.setCornerRadius(999f);
        tvNewBadge.setBackground(badge);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) return true;
            if (item.getItemId() == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class).putExtra("search_query", ""));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void requestNotificationPermission() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> { });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
