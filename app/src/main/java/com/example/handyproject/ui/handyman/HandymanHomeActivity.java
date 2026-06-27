package com.example.handyproject.ui.handyman;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.adapters.EnquiryAdapter;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.customer.MessagesActivity;
import com.example.handyproject.ui.customer.NotificationsActivity;
import com.example.handyproject.ui.customer.ProfileActivity;
import com.example.handyproject.ui.customer.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

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

    private SwitchMaterial switchAvailability;
    private TextView tvAvailabilityStatus;
    private TextView tvNewBadge;
    private RecyclerView rvEnquiries;
    private EnquiryAdapter enquiryAdapter;
    private final List<EnquiryItem> enquiries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_home);

        switchAvailability   = findViewById(R.id.switchAvailability);
        tvAvailabilityStatus = findViewById(R.id.tvAvailabilityStatus);
        tvNewBadge           = findViewById(R.id.tvNewBadge);
        rvEnquiries          = findViewById(R.id.rvEnquiries);

        setupAvailabilityToggle();
        setupEnquiries();
        setupBadge();
        setupBottomNav();

        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        ImageView ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ImageUtils.loadAvatar(ivUserAvatar, null);
        ivUserAvatar.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.btnAddPhotos).setOnClickListener(v ->
                Toast.makeText(this, "Portfolio upload coming soon",
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnViewAllEnquiries).setOnClickListener(v ->
                Toast.makeText(this, "Enquiries coming soon",
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.tvViewAllPortfolio).setOnClickListener(v ->
                Toast.makeText(this, "Portfolio coming soon",
                        Toast.LENGTH_SHORT).show());
    }

    private void setupAvailabilityToggle() {
        switchAvailability.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvAvailabilityStatus.setText("Available");
                tvAvailabilityStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                tvAvailabilityStatus.setText("Unavailable");
                tvAvailabilityStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.colorTextSecondary));
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
}
