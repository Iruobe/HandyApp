package com.example.handyproject.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.handyproject.R;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.ui.customer.ServiceMenu;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegistration extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private TextInputLayout tilFullName, tilEmail, tilPhone, tilLocation,
            tilPassword, tilConfirmPassword;
    private MaterialButton signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

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
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, ServiceMenu.class));
            finish();
        }
    }

    private boolean validate(String fullName, String email, String phone,
                             String location, String password, String confirmPassword) {
        boolean valid = true;
        valid &= ValidationUtils.validateRequired(tilFullName, fullName, "Full name");
        valid &= ValidationUtils.validateEmail(tilEmail, email);
        valid &= ValidationUtils.validateRequired(tilPhone, phone, "Phone number");
        valid &= ValidationUtils.validateRequired(tilLocation, location, "Location");
        valid &= ValidationUtils.validatePassword(tilPassword, password);
        valid &= ValidationUtils.validateConfirmPassword(tilConfirmPassword, password, confirmPassword);
        return valid;
    }

    private void signupButtonClicked() {
        String fullName        = ViewUtils.getTextFrom(tilFullName);
        String email           = ViewUtils.getTextFrom(tilEmail);
        String phone           = ViewUtils.getTextFrom(tilPhone);
        String location        = ViewUtils.getTextFrom(tilLocation);
        String password        = ViewUtils.getTextFrom(tilPassword);
        String confirmPassword = ViewUtils.getTextFrom(tilConfirmPassword);

        if (!validate(fullName, email, phone, location, password, confirmPassword)) return;

        signupButton.setEnabled(false);

        authRepository.createAccount(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                String uid = user.getUid();

                Map<String, Object> userData = new HashMap<>();
                userData.put(Constants.FIELD_UID, uid);
                userData.put(Constants.FIELD_ROLE, Constants.ROLE_CUSTOMER);
                userData.put(Constants.FIELD_FULL_NAME, fullName);
                userData.put(Constants.FIELD_EMAIL, email);
                userData.put(Constants.FIELD_PHONE, phone);
                userData.put(Constants.FIELD_LOCATION, location);
                userData.put(Constants.FIELD_CREATED_AT, Timestamp.now());

                userRepository.createUser(uid, userData, new UserRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(CustomerRegistration.this, ServiceMenu.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(CustomerRegistration.this,
                                "Failed to save profile. Please try again.",
                                Toast.LENGTH_SHORT).show();
                        signupButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CustomerRegistration.this, message, Toast.LENGTH_LONG).show();
                signupButton.setEnabled(true);
            }
        });
    }
}
