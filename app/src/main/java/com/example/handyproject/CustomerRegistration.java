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

import java.util.HashMap;
import java.util.Map;

public class CustomerRegistration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation,
            tilPassword, tilConfirmPassword;
    private MaterialButton signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tilFullName        = findViewById(R.id.tilFullName);
        tilEmail           = findViewById(R.id.tilEmail);
        tilPhone           = findViewById(R.id.tilPhone);
        tilLocation        = findViewById(R.id.tilLocation);
        tilPassword        = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        signupButton       = findViewById(R.id.button);

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

    private boolean validate(String fullName, String email, String phone,
                             String location, String password, String confirmPassword) {
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
        String fullName        = getTextFrom(tilFullName);
        String email           = getTextFrom(tilEmail);
        String phone           = getTextFrom(tilPhone);
        String location        = getTextFrom(tilLocation);
        String password        = getTextFrom(tilPassword);
        String confirmPassword = getTextFrom(tilConfirmPassword);

        if (!validate(fullName, email, phone, location, password, confirmPassword)) return;

        signupButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", uid);
                        userData.put("role", "customer");
                        userData.put("fullName", fullName);
                        userData.put("email", email);
                        userData.put("phoneNumber", phone);
                        userData.put("location", location);
                        userData.put("createdAt", Timestamp.now());

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(this, ServiceMenu.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("CustomerRegistration", "Error writing document", e);
                                    Toast.makeText(this, "Failed to save profile. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                    signupButton.setEnabled(true);
                                });
                    } else {
                        Log.w("CustomerRegistration", "createUserWithEmail:failure", task.getException());
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
