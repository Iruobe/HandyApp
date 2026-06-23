package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.handyproject.R;
import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private TextInputLayout tilFullName, tilPhone, tilLocation, tilEmail,
            tilServiceCategory, tilServiceDescription, tilHourlyRate, tilBio, tilResponseTime;
    private MaterialAutoCompleteTextView actvResponseTime;
    private MaterialButton btnSave;

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
        btnSave                  = findViewById(R.id.btnSave);

        actvResponseTime = findViewById(R.id.etResponseTime);
        actvResponseTime.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, Constants.RESPONSE_TIME_OPTIONS));

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
