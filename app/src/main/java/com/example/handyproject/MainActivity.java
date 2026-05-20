package com.example.handyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        tilEmail    = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        loginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> loginButtonClicked());

        findViewById(R.id.RegisterLinkTextView).setOnClickListener(v ->
                startActivity(new Intent(this, HandymanRegistration.class)));

        findViewById(R.id.RegisterLinkTextView2).setOnClickListener(v ->
                startActivity(new Intent(this, CustomerRegistration.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchRoleAndNavigate(currentUser.getUid());
        }
    }

    private boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            valid = false;
        } else { tilEmail.setError(null); }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            valid = false;
        } else { tilPassword.setError(null); }

        return valid;
    }

    private void loginButtonClicked() {
        String email    = getTextFrom(tilEmail);
        String password = getTextFrom(tilPassword);

        if (!validate(email, password)) return;

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        fetchRoleAndNavigate(user.getUid());
                    } else {
                        setLoading(false);
                        Log.w("MainActivity", "signInWithEmail:failure", task.getException());
                        String msg = task.getException() != null
                                ? task.getException().getMessage() : "Login failed.";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchRoleAndNavigate(String uid) {
        setLoading(true);

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");
                        if ("handyman".equals(role)) {
                            // TODO: Replace with HandymanHomeActivity once created in Phase 2
                            startActivity(new Intent(this, ServiceMenu.class));
                        } else {
                            // TODO: Replace with CustomerHomeActivity once created in Phase 2
                            startActivity(new Intent(this, ServiceMenu.class));
                        }
                        finish();
                    } else {
                        mAuth.signOut();
                        setLoading(false);
                        Toast.makeText(this,
                                "Account not found. Please register again.",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Log.w("MainActivity", "Error fetching user role", e);
                    Toast.makeText(this,
                            "Failed to load your account. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
    }

    private String getTextFrom(TextInputLayout til) {
        return til.getEditText() != null
                ? til.getEditText().getText().toString().trim() : "";
    }
}
