package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.adapters.HandymanHomeAdapter;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private MaterialCardView cardAgentBanner;
    private RecyclerView rvHandymen;
    private LinearLayout layoutUpload;
    private ActivityResultLauncher<String> imagePicker;
    private final List<Handyman> handymen = new ArrayList<>();
    private HandymanRepository handymanRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        ViewUtils.fixNavOverlap(findViewById(R.id.scrollContent), findViewById(R.id.bottomNav));

        tvGreeting      = findViewById(R.id.tvGreeting);
        cardAgentBanner = findViewById(R.id.cardAgentBanner);
        rvHandymen      = findViewById(R.id.rvHandymen);
        layoutUpload    = findViewById(R.id.layoutUpload);

        setupImagePicker();
        loadUserGreeting();
        applyAgentBannerGradient();
        setupHandymenCards();
        setupBottomNav();

        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        ImageView ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ImageUtils.loadAvatar(ivUserAvatar, null);
        ivUserAvatar.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText().toString().trim();
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("search_query", query);
            startActivity(intent);
            return true;
        });

        findViewById(R.id.btnFindHandymen).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("search_query", "");
            startActivity(intent);
        });

        findViewById(R.id.tvSeeAll).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("search_query", "");
            startActivity(intent);
        });
    }

    private void loadUserGreeting() {
        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();
        if (user == null) return;

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String fullName = doc.getString(Constants.FIELD_FULL_NAME);
                    tvGreeting.setText("Hello, " + (fullName != null ? fullName : "there"));
                })
                .addOnFailureListener(e -> tvGreeting.setText("Hello there"));
    }

    private void applyAgentBannerGradient() {
        LinearLayout layoutAgentBanner = findViewById(R.id.layoutAgentBanner);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        ContextCompat.getColor(this, R.color.colorPrimary),
                        ContextCompat.getColor(this, R.color.colorPrimaryDark)
                });
        gradient.setCornerRadius(getResources().getDimension(R.dimen.corner_radius));
        layoutAgentBanner.setBackground(gradient);
    }

    private void setupImagePicker() {
        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Toast.makeText(this,
                                "Photo selected — agent feature coming soon",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        layoutUpload.setOnClickListener(v -> imagePicker.launch("image/*"));
    }

    private void setupHandymenCards() {
        rvHandymen.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        HandymanHomeAdapter adapter = new HandymanHomeAdapter(this, handymen);
        rvHandymen.setAdapter(adapter);

        handymanRepository = new HandymanRepository();
        handymanRepository.startListening(new HandymanRepository.HandymanListCallback() {
            @Override
            public void onUpdate(List<Handyman> result) {
                handymen.clear();
                handymen.addAll(result);
                adapter.notifyDataSetChanged();
                toggleHandymenSection();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CustomerHomeActivity.this, message, Toast.LENGTH_SHORT).show();
                toggleHandymenSection();
            }
        });
    }

    private void toggleHandymenSection() {
        int visibility = handymen.isEmpty() ? View.GONE : View.VISIBLE;
        findViewById(R.id.layoutAvailableHandymen).setVisibility(visibility);
        rvHandymen.setVisibility(visibility);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handymanRepository != null) handymanRepository.stopListening();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) return true;
            if (item.getItemId() == R.id.nav_search) {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("search_query", "");
                startActivity(intent);
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
}
