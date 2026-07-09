package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Notification;
import com.example.handyproject.data.repository.NotificationRepository;
import com.example.handyproject.ui.common.adapters.NotificationAdapter;
import com.example.handyproject.ui.common.utils.NavigationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private LinearLayout layoutFilterChips;
    private RecyclerView rvNotifications;
    private LinearLayout layoutEmptyState;
    private NotificationAdapter adapter;
    private final NotificationRepository notificationRepository = new NotificationRepository();

    private final List<Notification> allNotifications = new ArrayList<>();
    private final String[] chipLabels = {"All", "Bookings", "Messages", "System"};
    private final String[] chipTypes  = {null, Constants.TYPE_BOOKING, Constants.TYPE_MESSAGE, Constants.TYPE_SYSTEM};
    private final MaterialCardView[] chipCards = new MaterialCardView[4];
    private int selectedChipIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ViewUtils.fixNavOverlap(findViewById(R.id.rvNotifications), findViewById(R.id.bottomNav));

        layoutFilterChips = findViewById(R.id.layoutFilterChips);
        rvNotifications   = findViewById(R.id.rvNotifications);
        layoutEmptyState  = findViewById(R.id.layoutEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        buildFilterChips();
        setupRecyclerView();
        setupBottomNav();
    }

    private void buildFilterChips() {
        layoutFilterChips.removeAllViews();

        int cornerPx    = getResources().getDimensionPixelSize(R.dimen.corner_radius);
        int marginEndPx = getResources().getDimensionPixelSize(R.dimen.padding_small);
        int padHPx      = getResources().getDimensionPixelSize(R.dimen.padding_standard);
        int padVPx      = getResources().getDimensionPixelSize(R.dimen.padding_small);

        for (int i = 0; i < chipLabels.length; i++) {
            final int index = i;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMarginEnd(marginEndPx);
            card.setLayoutParams(cardParams);
            card.setRadius(cornerPx);
            card.setCardElevation(0f);
            card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.divider_height));

            TextView tv = new TextView(this);
            tv.setText(chipLabels[i]);
            tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_body));
            tv.setPadding(padHPx, padVPx, padHPx, padVPx);
            card.addView(tv);

            applyChipStyle(card, tv, index == selectedChipIndex);

            card.setOnClickListener(v -> {
                selectedChipIndex = index;
                for (int j = 0; j < chipCards.length; j++) {
                    applyChipStyle(chipCards[j], (TextView) chipCards[j].getChildAt(0),
                            j == selectedChipIndex);
                }
                applyFilter();
            });

            chipCards[i] = card;
            layoutFilterChips.addView(card);
        }
    }

    private void applyChipStyle(MaterialCardView card, TextView tv, boolean selected) {
        if (selected) {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            card.setStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorCardBackground));
            card.setStrokeColor(ContextCompat.getColor(this, R.color.colorUnselectedBorder));
            tv.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
        }
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this, notification ->
                notificationRepository.markAsRead(notification.getId()));
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void applyFilter() {
        String type = chipTypes[selectedChipIndex];
        List<Notification> filtered = new ArrayList<>();
        if (type == null) {
            filtered.addAll(allNotifications);
        } else {
            for (Notification notification : allNotifications) {
                if (type.equals(notification.getType())) {
                    filtered.add(notification);
                }
            }
        }
        adapter.updateData(filtered);

        boolean isEmpty = filtered.isEmpty();
        rvNotifications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                NavigationUtils.goHome(this);
                return true;
            }
            if (item.getItemId() == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
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

    @Override
    protected void onStart() {
        super.onStart();
        notificationRepository.startListening(new NotificationRepository.NotificationListCallback() {
            @Override
            public void onUpdate(List<Notification> notifications) {
                allNotifications.clear();
                allNotifications.addAll(notifications);
                applyFilter();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(NotificationsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationRepository.stopListening();
    }
}
