package com.example.handyproject.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.handyproject.R;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.ui.handyman.HandymanHomeActivity;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HandymanRegistration extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation,
            tilServiceCategory, tilServiceDescription, tilYearsExperience,
            tilHourlyRate, tilPassword, tilConfirmPassword;
    private MaterialButton signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_registration);

        tilFullName           = findViewById(R.id.tilFullName);
        tilEmail              = findViewById(R.id.tilEmail);
        tilPhone              = findViewById(R.id.tilPhone);
        tilLocation           = findViewById(R.id.tilLocation);
        tilServiceCategory    = findViewById(R.id.tilServiceCategory);
        tilServiceDescription = findViewById(R.id.tilServiceDescription);
        tilYearsExperience    = findViewById(R.id.tilYearsExperience);
        tilHourlyRate         = findViewById(R.id.tilHourlyRate);
        tilPassword           = findViewById(R.id.tilPassword);
        tilConfirmPassword    = findViewById(R.id.tilConfirmPassword);
        signupButton          = findViewById(R.id.button);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        signupButton.setOnClickListener(v -> signupButtonClicked());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, HandymanHomeActivity.class));
            finish();
        }
    }

    private boolean validate(String fullName, String email, String phone, String location,
                             String serviceCategory, String serviceDescription,
                             String yearsExp, String hourlyRate,
                             String password, String confirmPassword) {
        boolean valid = true;
        valid &= ValidationUtils.validateRequired(tilFullName, fullName, "Full name");
        valid &= ValidationUtils.validateEmail(tilEmail, email);
        valid &= ValidationUtils.validateRequired(tilPhone, phone, "Phone number");
        valid &= ValidationUtils.validateRequired(tilLocation, location, "Location");
        valid &= ValidationUtils.validateRequired(tilServiceCategory, serviceCategory, "Service category");
        valid &= ValidationUtils.validateRequired(tilServiceDescription, serviceDescription, "Service description");
        valid &= ValidationUtils.validateRequired(tilYearsExperience, yearsExp, "Years of experience");
        valid &= ValidationUtils.validatePositiveDouble(tilHourlyRate, hourlyRate, "Hourly rate");
        valid &= ValidationUtils.validatePassword(tilPassword, password);
        valid &= ValidationUtils.validateConfirmPassword(tilConfirmPassword, password, confirmPassword);
        return valid;
    }

    private void signupButtonClicked() {
        String fullName           = ViewUtils.getTextFrom(tilFullName);
        String email              = ViewUtils.getTextFrom(tilEmail);
        String phone              = ViewUtils.getTextFrom(tilPhone);
        String location           = ViewUtils.getTextFrom(tilLocation);
        String serviceCategory    = ViewUtils.getTextFrom(tilServiceCategory);
        String serviceDescription = ViewUtils.getTextFrom(tilServiceDescription);
        String yearsExp           = ViewUtils.getTextFrom(tilYearsExperience);
        String hourlyRateStr      = ViewUtils.getTextFrom(tilHourlyRate);
        String password           = ViewUtils.getTextFrom(tilPassword);
        String confirmPassword    = ViewUtils.getTextFrom(tilConfirmPassword);

        if (!validate(fullName, email, phone, location, serviceCategory, serviceDescription,
                yearsExp, hourlyRateStr, password, confirmPassword)) return;

        signupButton.setEnabled(false);
        double hourlyRate     = Double.parseDouble(hourlyRateStr);
        int yearsOfExperience = Integer.parseInt(yearsExp);

        authRepository.createAccount(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                String uid = user.getUid();

                Map<String, Object> userData = new HashMap<>();
                userData.put(Constants.FIELD_UID, uid);
                userData.put(Constants.FIELD_ROLE, Constants.ROLE_HANDYMAN);
                userData.put(Constants.FIELD_FULL_NAME, fullName);
                userData.put(Constants.FIELD_EMAIL, email);
                userData.put(Constants.FIELD_PHONE, phone);
                userData.put(Constants.FIELD_LOCATION, location);
                userData.put(Constants.FIELD_SERVICE_CATEGORY, serviceCategory);
                userData.put(Constants.FIELD_SERVICE_DESCRIPTION, serviceDescription);
                userData.put(Constants.FIELD_YEARS_EXPERIENCE, yearsOfExperience);
                userData.put(Constants.FIELD_HOURLY_RATE, hourlyRate);
                userData.put(Constants.FIELD_AVAILABLE_FOR_HIRE, true);
                userData.put(Constants.FIELD_PORTFOLIO_PHOTOS, new ArrayList<>());
                userData.put(Constants.FIELD_RATING, 0);
                userData.put(Constants.FIELD_TOTAL_JOBS, 0);
                userData.put(Constants.FIELD_PROFILE_VIEWS, 0);
                userData.put(Constants.FIELD_CREATED_AT, Timestamp.now());

                userRepository.createUser(uid, userData, new UserRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Registration", "Firestore write successful");
                        Toast.makeText(HandymanRegistration.this,
                                "Profile saved successfully",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HandymanRegistration.this, HandymanHomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e("Registration", "Firestore write failed: " + message);
                        Toast.makeText(HandymanRegistration.this,
                                "Failed to save profile: " + message,
                                Toast.LENGTH_LONG).show();
                        signupButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(HandymanRegistration.this, message, Toast.LENGTH_LONG).show();
                signupButton.setEnabled(true);
            }
        });
    }
}
