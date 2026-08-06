package com.example.handyproject.ui.handyman;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.adapters.EnquiryAdapter;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.ui.customer.MessagesActivity;
import com.example.handyproject.ui.customer.NotificationsActivity;
import com.example.handyproject.ui.customer.ProfileActivity;
import com.example.handyproject.ui.customer.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

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
    private TextView tvRating;
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
        tvNewBadge  = findViewById(R.id.tvNewBadge);
        rvEnquiries = findViewById(R.id.rvEnquiries);

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

    /** Mirrors HandymanProfileActivity's header: real synced handyman.rating, same formatting. */
    private void loadRating() {
        AuthRepository authRepository = new AuthRepository();
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        String uid = firebaseUser != null ? firebaseUser.getUid() : "";
        if (uid.isEmpty()) return;

        handymanRepository.fetchHandyman(uid, new HandymanRepository.HandymanCallback() {
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
        enquiries.add(new EnquiryItem(
                "Sarah Jenkins",
                "Need help assembling a large IKEA wardrobe...",
                "2h ago", "New"));
        enquiries.add(new EnquiryItem(
                "Mike Chen",
                "Leaky faucet in the master bathroom.",
                "Yesterday", "Responded"));
        enquiries.add(new EnquiryItem(
                "Amanda R.",
                "TV mounting on drywall, 65 inch screen.",
                "Oct 12", "Booked"));

        rvEnquiries.setLayoutManager(new LinearLayoutManager(this));
        enquiryAdapter = new EnquiryAdapter(enquiries);
        rvEnquiries.setAdapter(enquiryAdapter);
        rvEnquiries.setNestedScrollingEnabled(false);
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
