package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.handyproject.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HandymanProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
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

        tvHandymanName.setText("Marcus Johnson");
        tvHourlyRate.setText("£65");
        tvServiceCategory.setText("Master Carpenter & General Repair");
        tvLocation.setText("London, UK (2.4 miles away)");
        tvRating.setText("4.9 (128 reviews)");

        viewPager.setAdapter(new ProfilePagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("About");
            else if (position == 1) tab.setText("Portfolio");
            else tab.setText("Reviews");
        }).attach();

        btnMessage.setOnClickListener(v ->
                Toast.makeText(this, "Messaging coming soon", Toast.LENGTH_SHORT).show());
        btnBookNow.setOnClickListener(v ->
                Toast.makeText(this, "Booking coming soon", Toast.LENGTH_SHORT).show());
    }

    private static class ProfilePagerAdapter extends FragmentStateAdapter {

        ProfilePagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new ProfileAboutFragment();
            if (position == 1) return new ProfilePortfolioFragment();
            return new ProfileReviewsFragment();
        }
    }
}
