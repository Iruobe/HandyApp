package com.example.handyproject.ui.customer;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.PortfolioRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.utils.ImageCompressor;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.common.utils.ServicesInputHelper;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PortfolioRepository portfolioRepository = new PortfolioRepository();

    private TextInputLayout tilFullName, tilPhone, tilLocation, tilEmail,
            tilServiceCategory, tilServiceDescription, tilHourlyRate, tilBio, tilResponseTime;
    private MaterialAutoCompleteTextView actvResponseTime;
    private TextView tvServicesOfferedLabel;
    private LinearLayout llServicesContainer;
    private MaterialButton btnAddService;
    private ServicesInputHelper servicesInputHelper;
    private MaterialButton btnSave;

    private TextView tvPortfolioLabel;
    private HorizontalScrollView scrollPortfolio;
    private LinearLayout llPortfolioContainer;
    private ActivityResultLauncher<String> portfolioImagePicker;
    private final List<String> portfolioPhotos = new ArrayList<>();
    private boolean portfolioBusy = false;

    private String currentUid;
    private String currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tilFullName            = findViewById(R.id.tilFullName);
        tilPhone                = findViewById(R.id.tilPhone);
        tilLocation              = findViewById(R.id.tilLocation);
        tilEmail                 = findViewById(R.id.tilEmail);
        tilServiceCategory       = findViewById(R.id.tilServiceCategory);
        tilServiceDescription    = findViewById(R.id.tilServiceDescription);
        tilHourlyRate            = findViewById(R.id.tilHourlyRate);
        tilBio                   = findViewById(R.id.tilBio);
        tilResponseTime          = findViewById(R.id.tilResponseTime);
        tvServicesOfferedLabel   = findViewById(R.id.tvServicesOfferedLabel);
        llServicesContainer      = findViewById(R.id.llServicesContainer);
        btnAddService            = findViewById(R.id.btnAddService);
        btnSave                  = findViewById(R.id.btnSave);

        tvPortfolioLabel     = findViewById(R.id.tvPortfolioLabel);
        scrollPortfolio      = findViewById(R.id.scrollPortfolio);
        llPortfolioContainer = findViewById(R.id.llPortfolioContainer);

        actvResponseTime = findViewById(R.id.etResponseTime);
        actvResponseTime.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, Constants.RESPONSE_TIME_OPTIONS));

        servicesInputHelper = new ServicesInputHelper(this, llServicesContainer, btnAddService);

        portfolioImagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(), this::handlePickedPortfolioImage);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());

        loadUser();
    }

    private void loadUser() {
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "You're not signed in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUid = firebaseUser.getUid();

        userRepository.fetchUser(currentUid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    Toast.makeText(EditProfileActivity.this,
                            "Could not load your profile.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                currentRole = user.getRole();
                prefill(user);
                applyRoleVisibility();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void prefill(User user) {
        setText(tilFullName, user.getFullName());
        setText(tilPhone, user.getPhoneNumber());
        setText(tilLocation, user.getLocation());
        setText(tilEmail, user.getEmail());
        setText(tilServiceCategory, user.getServiceCategory());
        setText(tilServiceDescription, user.getServiceDescription());
        if (user.getHourlyRate() > 0) {
            setText(tilHourlyRate, String.valueOf(user.getHourlyRate()));
        }
        setText(tilBio, user.getBio());
        actvResponseTime.setText(
                user.getResponseTime() != null && !user.getResponseTime().isEmpty()
                        ? user.getResponseTime() : Constants.DEFAULT_RESPONSE_TIME,
                false);
        servicesInputHelper.loadServices(user.getServicesOffered());

        portfolioPhotos.clear();
        if (user.getPortfolioPhotos() != null) {
            portfolioPhotos.addAll(user.getPortfolioPhotos());
        }
        renderPortfolioTiles();
    }

    private void setText(TextInputLayout til, String value) {
        if (til.getEditText() != null) {
            til.getEditText().setText(value != null ? value : "");
        }
    }

    private void applyRoleVisibility() {
        int visibility = Constants.ROLE_HANDYMAN.equals(currentRole) ? View.VISIBLE : View.GONE;
        tilServiceCategory.setVisibility(visibility);
        tilServiceDescription.setVisibility(visibility);
        tilHourlyRate.setVisibility(visibility);
        tilBio.setVisibility(visibility);
        tilResponseTime.setVisibility(visibility);
        tvServicesOfferedLabel.setVisibility(visibility);
        llServicesContainer.setVisibility(visibility);
        btnAddService.setVisibility(visibility);
        tvPortfolioLabel.setVisibility(visibility);
        scrollPortfolio.setVisibility(visibility);
    }

    private void renderPortfolioTiles() {
        llPortfolioContainer.removeAllViews();
        int tileSize = getResources().getDimensionPixelSize(R.dimen.card_height_portfolio);
        int tileMargin = getResources().getDimensionPixelSize(R.dimen.padding_small);

        for (int i = 0; i < Constants.MAX_PORTFOLIO_PHOTOS; i++) {
            View tile = (i < portfolioPhotos.size())
                    ? buildFilledPortfolioTile(portfolioPhotos.get(i))
                    : buildEmptyPortfolioTile();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tileSize, tileSize);
            if (i < Constants.MAX_PORTFOLIO_PHOTOS - 1) {
                params.rightMargin = tileMargin;
            }
            llPortfolioContainer.addView(tile, params);
        }
    }

    private View buildFilledPortfolioTile(String url) {
        FrameLayout frame = new FrameLayout(this);

        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(getResources().getDimension(R.dimen.corner_radius));
        card.setCardElevation(0f);
        card.setStrokeWidth(0);
        frame.addView(card, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(iv, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageUtils.loadImage(iv, url);

        int removeSize = getResources().getDimensionPixelSize(R.dimen.avatar_edit_button_size);
        int inset = getResources().getDimensionPixelSize(R.dimen.padding_xsmall);
        ImageButton remove = new ImageButton(this);
        remove.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_filled));
        remove.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorError));
        remove.setImageResource(R.drawable.ic_close);
        remove.setColorFilter(Color.WHITE);
        remove.setPadding(inset, inset, inset, inset);
        remove.setContentDescription("Remove photo");
        remove.setOnClickListener(v -> removePortfolioImage(url));

        FrameLayout.LayoutParams removeParams = new FrameLayout.LayoutParams(removeSize, removeSize);
        removeParams.gravity = Gravity.TOP | Gravity.END;
        removeParams.topMargin = inset;
        removeParams.rightMargin = inset;
        frame.addView(remove, removeParams);

        return frame;
    }

    private View buildEmptyPortfolioTile() {
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(getResources().getDimension(R.dimen.corner_radius));
        card.setCardElevation(0f);
        card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.divider_height));
        card.setStrokeColor(ContextCompat.getColor(this, R.color.colorUnselectedBorder));
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorUnselectedBackground));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> {
            if (!portfolioBusy) portfolioImagePicker.launch("image/*");
        });

        ImageView plus = new ImageView(this);
        plus.setImageResource(R.drawable.ic_add);
        plus.setColorFilter(ContextCompat.getColor(this, R.color.colorUnselectedIcon));
        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        FrameLayout.LayoutParams plusParams = new FrameLayout.LayoutParams(iconSize, iconSize);
        plusParams.gravity = Gravity.CENTER;
        card.addView(plus, plusParams);

        return card;
    }

    private void handlePickedPortfolioImage(Uri uri) {
        if (uri == null || portfolioBusy) return;

        if (portfolioPhotos.size() >= Constants.MAX_PORTFOLIO_PHOTOS) {
            Toast.makeText(this, "You can only have up to 4 portfolio photos", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] compressed;
        try {
            compressed = ImageCompressor.compress(this, uri);
        } catch (IOException e) {
            Toast.makeText(this, "Couldn't read that image", Toast.LENGTH_SHORT).show();
            return;
        }

        setPortfolioBusy(true);
        portfolioRepository.uploadPortfolioImage(currentUid, compressed, new PortfolioRepository.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                portfolioPhotos.add(downloadUrl);
                renderPortfolioTiles();
                setPortfolioBusy(false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
                setPortfolioBusy(false);
            }
        });
    }

    private void removePortfolioImage(String url) {
        if (portfolioBusy) return;

        setPortfolioBusy(true);
        portfolioRepository.deletePortfolioImage(currentUid, url, new PortfolioRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                portfolioPhotos.remove(url);
                renderPortfolioTiles();
                setPortfolioBusy(false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
                setPortfolioBusy(false);
            }
        });
    }

    private void setPortfolioBusy(boolean busy) {
        portfolioBusy = busy;
        llPortfolioContainer.setAlpha(busy ? 0.5f : 1f);
    }

    private void saveChanges() {
        String fullName = ViewUtils.getTextFrom(tilFullName);
        String phone     = ViewUtils.getTextFrom(tilPhone);
        String location  = ViewUtils.getTextFrom(tilLocation);

        boolean valid = true;
        valid &= ValidationUtils.validateRequired(tilFullName, fullName, "Full name");
        valid &= ValidationUtils.validateRequired(tilPhone, phone, "Phone number");
        valid &= ValidationUtils.validateRequired(tilLocation, location, "Location");

        boolean isHandyman = Constants.ROLE_HANDYMAN.equals(currentRole);
        String serviceCategory = "";
        String serviceDescription = "";
        String hourlyRateStr = "";
        String bio = "";
        String responseTime = "";

        if (isHandyman) {
            serviceCategory    = ViewUtils.getTextFrom(tilServiceCategory);
            serviceDescription = ViewUtils.getTextFrom(tilServiceDescription);
            hourlyRateStr      = ViewUtils.getTextFrom(tilHourlyRate);
            bio                = ViewUtils.getTextFrom(tilBio);
            responseTime       = ViewUtils.getTextFrom(tilResponseTime);

            valid &= ValidationUtils.validateRequired(tilServiceCategory, serviceCategory, "Service category");
            valid &= ValidationUtils.validateRequired(tilServiceDescription, serviceDescription, "Service description");
            valid &= ValidationUtils.validatePositiveDouble(tilHourlyRate, hourlyRateStr, "Hourly rate");
        }

        if (!valid) return;

        List<String> servicesOffered = new ArrayList<>();
        if (isHandyman) {
            servicesOffered = servicesInputHelper.getServices();
            if (servicesOffered.isEmpty()) {
                Toast.makeText(this, "Add at least one service", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.FIELD_FULL_NAME, fullName);
        updates.put(Constants.FIELD_PHONE, phone);
        updates.put(Constants.FIELD_LOCATION, location);

        if (isHandyman) {
            updates.put(Constants.FIELD_SERVICE_CATEGORY, serviceCategory);
            updates.put(Constants.FIELD_SERVICE_DESCRIPTION, serviceDescription);
            updates.put(Constants.FIELD_HOURLY_RATE, Double.parseDouble(hourlyRateStr));
            updates.put(Constants.FIELD_BIO, bio);
            updates.put(Constants.FIELD_RESPONSE_TIME,
                    responseTime.isEmpty() ? Constants.DEFAULT_RESPONSE_TIME : responseTime);
            updates.put(Constants.FIELD_SERVICES_OFFERED, servicesOffered);
        }

        btnSave.setEnabled(false);
        userRepository.updateUser(currentUid, updates, new UserRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
            }
        });
    }
}
