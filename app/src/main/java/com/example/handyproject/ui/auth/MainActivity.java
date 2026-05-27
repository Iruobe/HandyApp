package com.example.handyproject.ui.auth;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.utils.ValidationUtils;
import com.example.handyproject.ui.common.utils.ViewUtils;
import com.example.handyproject.ui.customer.CustomerHomeActivity;
import com.example.handyproject.ui.handyman.HandymanHomeActivity;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyGradient();

        tilEmail    = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        loginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> loginButtonClicked());

        findViewById(R.id.passkeyButton).setOnClickListener(v ->
                Toast.makeText(this, "Passkey coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.forgotPasswordText).setOnClickListener(v ->
                Toast.makeText(this, "Forgot password coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.RegisterLinkTextView).setOnClickListener(v ->
                startActivity(new Intent(this, RoleSelectionActivity.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            fetchRoleAndNavigate(currentUser.getUid());
        }
    }

    private void applyGradient() {
        ConstraintLayout root = findViewById(R.id.mainRoot);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, R.color.colorPrimary),
                        ContextCompat.getColor(this, R.color.colorPrimaryDark)
                });
        root.setBackground(gradient);
    }

    private boolean validate(String email, String password) {
        boolean emailOk    = ValidationUtils.validateEmail(tilEmail, email);
        boolean passwordOk = ValidationUtils.validatePassword(tilPassword, password);
        return emailOk && passwordOk;
    }

    private void loginButtonClicked() {
        String email    = ViewUtils.getTextFrom(tilEmail);
        String password = ViewUtils.getTextFrom(tilPassword);

        if (!validate(email, password)) return;

        ViewUtils.setLoading(progressBar, loginButton, true);

        authRepository.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                fetchRoleAndNavigate(user.getUid());
            }

            @Override
            public void onFailure(String message) {
                ViewUtils.setLoading(progressBar, loginButton, false);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchRoleAndNavigate(String uid) {
        ViewUtils.setLoading(progressBar, loginButton, true);

        userRepository.fetchUser(uid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    authRepository.signOut();
                    ViewUtils.setLoading(progressBar, loginButton, false);
                    Toast.makeText(MainActivity.this,
                            "Account not found. Please register again.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String role = user.getRole();
                Intent intent;
                if (Constants.ROLE_CUSTOMER.equals(role)) {
                    intent = new Intent(MainActivity.this, CustomerHomeActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, HandymanHomeActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                ViewUtils.setLoading(progressBar, loginButton, false);
                Toast.makeText(MainActivity.this,
                        "Failed to load your account. Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
