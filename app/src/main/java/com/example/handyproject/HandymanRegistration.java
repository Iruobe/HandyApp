package com.example.handyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HandymanRegistration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation,
            tilServiceCategory, tilServiceDescription, tilYearsExperience,
            tilHourlyRate, tilPassword, tilConfirmPassword;
    private MaterialButton signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        signupButton.setOnClickListener(v -> signupButtonClicked());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, ServiceMenu.class));
            finish();
        }
    }

    private boolean validate(String fullName, String email, String phone, String location,
                             String serviceCategory, String serviceDescription,
                             String yearsExp, String hourlyRate,
                             String password, String confirmPassword) {
        boolean valid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError("Full name is required");
            valid = false;
        } else { tilFullName.setError(null); }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            valid = false;
        } else { tilEmail.setError(null); }

        if (phone.isEmpty()) {
            tilPhone.setError("Phone number is required");
            valid = false;
        } else { tilPhone.setError(null); }

        if (location.isEmpty()) {
            tilLocation.setError("Location is required");
            valid = false;
        } else { tilLocation.setError(null); }

        if (serviceCategory.isEmpty()) {
            tilServiceCategory.setError("Service category is required");
            valid = false;
        } else { tilServiceCategory.setError(null); }

        if (serviceDescription.isEmpty()) {
            tilServiceDescription.setError("Service description is required");
            valid = false;
        } else { tilServiceDescription.setError(null); }

        if (yearsExp.isEmpty()) {
            tilYearsExperience.setError("Years of experience is required");
            valid = false;
        } else { tilYearsExperience.setError(null); }

        if (hourlyRate.isEmpty()) {
            tilHourlyRate.setError("Hourly rate is required");
            valid = false;
        } else {
            try {
                double rate = Double.parseDouble(hourlyRate);
                if (rate <= 0) {
                    tilHourlyRate.setError("Hourly rate must be greater than £0");
                    valid = false;
                } else { tilHourlyRate.setError(null); }
            } catch (NumberFormatException e) {
                tilHourlyRate.setError("Enter a valid hourly rate");
                valid = false;
            }
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            valid = false;
        } else if (password.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            valid = false;
        } else { tilPassword.setError(null); }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your password");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else { tilConfirmPassword.setError(null); }

        return valid;
    }

    private void signupButtonClicked() {
        String fullName           = getTextFrom(tilFullName);
        String email              = getTextFrom(tilEmail);
        String phone              = getTextFrom(tilPhone);
        String location           = getTextFrom(tilLocation);
        String serviceCategory    = getTextFrom(tilServiceCategory);
        String serviceDescription = getTextFrom(tilServiceDescription);
        String yearsExp           = getTextFrom(tilYearsExperience);
        String hourlyRateStr      = getTextFrom(tilHourlyRate);
        String password           = getTextFrom(tilPassword);
        String confirmPassword    = getTextFrom(tilConfirmPassword);

        if (!validate(fullName, email, phone, location, serviceCategory, serviceDescription,
                yearsExp, hourlyRateStr, password, confirmPassword)) return;

        signupButton.setEnabled(false);
        double hourlyRate     = Double.parseDouble(hourlyRateStr);
        int yearsOfExperience = Integer.parseInt(yearsExp);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", uid);
                        userData.put("role", "handyman");
                        userData.put("fullName", fullName);
                        userData.put("email", email);
                        userData.put("phoneNumber", phone);
                        userData.put("location", location);
                        userData.put("serviceCategory", serviceCategory);
                        userData.put("serviceDescription", serviceDescription);
                        userData.put("yearsOfExperience", yearsOfExperience);
                        userData.put("hourlyRate", hourlyRate);
                        userData.put("availableForHire", true);
                        userData.put("portfolioPhotos", new ArrayList<>());
                        userData.put("rating", 0);
                        userData.put("totalJobs", 0);
                        userData.put("profileViews", 0);
                        userData.put("createdAt", Timestamp.now());

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(this, ServiceMenu.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("HandymanRegistration", "Error writing document", e);
                                    Toast.makeText(this, "Failed to save profile. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                    signupButton.setEnabled(true);
                                });
                    } else {
                        Log.w("HandymanRegistration", "createUserWithEmail:failure", task.getException());
                        String msg = task.getException() != null
                                ? task.getException().getMessage() : "Registration failed.";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        signupButton.setEnabled(true);
                    }
                });
    }

    private String getTextFrom(TextInputLayout til) {
        return til.getEditText() != null
                ? til.getEditText().getText().toString().trim() : "";
    }
}
