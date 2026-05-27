package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import com.example.handyproject.ui.auth.MainActivity;
import com.example.handyproject.ui.common.adapters.HandymanHomeAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        tvGreeting      = findViewById(R.id.tvGreeting);
        cardAgentBanner = findViewById(R.id.cardAgentBanner);
        rvHandymen      = findViewById(R.id.rvHandymen);
        layoutUpload    = findViewById(R.id.layoutUpload);

        setupImagePicker();
        loadUserGreeting();
        applyAgentBannerGradient();
        setupHandymenCards();
        setupBottomNav();

        // TEMP: remove before launch
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            FirebaseService.getAuth().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnFindHandymen).setOnClickListener(v ->
                Toast.makeText(this, "Searching for handymen...", Toast.LENGTH_SHORT).show());

        findViewById(R.id.tvSeeAll).setOnClickListener(v ->
                startActivity(new Intent(this, ServiceMenu.class)));
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
        List<Handyman> dummyHandymen = new ArrayList<>();

        Handyman h1 = new Handyman();
        h1.setFullName("Marcus T.");
        h1.setServiceCategory("Plumbing & General");
        h1.setHourlyRate(45.0);
        h1.setRating(4.9);
        dummyHandymen.add(h1);

        Handyman h2 = new Handyman();
        h2.setFullName("Sarah J.");
        h2.setServiceCategory("Electrical & Smart Home");
        h2.setHourlyRate(60.0);
        h2.setRating(4.8);
        dummyHandymen.add(h2);

        Handyman h3 = new Handyman();
        h3.setFullName("David L.");
        h3.setServiceCategory("Carpentry & Assembly");
        h3.setHourlyRate(50.0);
        h3.setRating(5.0);
        dummyHandymen.add(h3);

        rvHandymen.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHandymen.setAdapter(new HandymanHomeAdapter(this, dummyHandymen));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) return true;
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
