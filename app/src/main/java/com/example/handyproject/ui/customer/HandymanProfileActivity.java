package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Locale;

public class HandymanProfileActivity extends AppCompatActivity {

    public static final String EXTRA_HANDYMAN_UID = "handyman_uid";

    private final HandymanRepository handymanRepository = new HandymanRepository();
    private String handymanUid;

    private MaterialToolbar toolbar;
    private ImageView ivCoverPhoto;
    private ShapeableImageView ivProfilePhoto;
    private TextView tvHandymanName;
    private TextView tvHourlyRate;
    private TextView tvServiceCategory;
    private TextView tvLocation;
    private TextView tvRating;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialButton btnMessage;
    private MaterialButton btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_profile);

        toolbar = findViewById(R.id.toolbar);
        ivCoverPhoto = findViewById(R.id.ivCoverPhoto);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvHandymanName = findViewById(R.id.tvHandymanName);
        tvHourlyRate = findViewById(R.id.tvHourlyRate);
        tvServiceCategory = findViewById(R.id.tvServiceCategory);
        tvLocation = findViewById(R.id.tvLocation);
        tvRating = findViewById(R.id.tvRating);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnMessage = findViewById(R.id.btnMessage);
        btnBookNow = findViewById(R.id.btnBookNow);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageUtils.loadImage(ivCoverPhoto, null);
        ImageUtils.loadAvatar(ivProfilePhoto, null);

        loadHandyman();

        btnMessage.setOnClickListener(v ->
                Toast.makeText(this, "Messaging coming soon", Toast.LENGTH_SHORT).show());
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra(EXTRA_HANDYMAN_UID, handymanUid);
            startActivity(intent);
        });
    }

    private void loadHandyman() {
        handymanUid = getIntent().getStringExtra(EXTRA_HANDYMAN_UID);
        if (handymanUid == null) {
            failAndFinish();
            return;
        }

        handymanRepository.fetchHandyman(handymanUid, new HandymanRepository.HandymanCallback() {
            @Override
            public void onSuccess(Handyman handyman) {
                if (handyman == null) {
                    failAndFinish();
                    return;
                }
                populateHeader(handyman);
            }

            @Override
            public void onError(String message) {
                failAndFinish();
            }
        });
    }

    private void populateHeader(Handyman handyman) {
        tvHandymanName.setText(handyman.getFullName() != null ? handyman.getFullName() : "");
        tvHourlyRate.setText(CurrencyUtils.formatAmount(handyman.getHourlyRate()));
        tvServiceCategory.setText(handyman.getServiceCategory() != null ? handyman.getServiceCategory() : "");
        tvLocation.setText(handyman.getLocation() != null ? handyman.getLocation() : "");
        tvRating.setText(handyman.getRating() > 0
                ? String.format(Locale.UK, "%.1f", handyman.getRating())
                : "Not rated");

        viewPager.setAdapter(new ProfilePagerAdapter(this, handyman));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("About");
            else if (position == 1) tab.setText("Portfolio");
            else tab.setText("Reviews");
        }).attach();
    }

    private void failAndFinish() {
        Toast.makeText(this, "Unable to load profile", Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class ProfilePagerAdapter extends FragmentStateAdapter {

        private final String bio;
        private final String responseTime;
        private final int yearsOfExperience;
        private final List<String> servicesOffered;

        ProfilePagerAdapter(@NonNull AppCompatActivity activity, @NonNull Handyman handyman) {
            super(activity);
            this.bio = handyman.getBio();
            this.responseTime = handyman.getResponseTime();
            this.yearsOfExperience = handyman.getYearsOfExperience();
            this.servicesOffered = handyman.getServicesOffered();
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return ProfileAboutFragment.newInstance(bio, responseTime, yearsOfExperience, servicesOffered);
            if (position == 1) return new ProfilePortfolioFragment();
            return new ProfileReviewsFragment();
        }
    }
}
