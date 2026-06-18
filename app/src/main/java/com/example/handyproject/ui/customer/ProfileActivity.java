package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.handyproject.R;
import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.auth.MainActivity;
import com.example.handyproject.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvRolePill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName     = findViewById(R.id.tvName);
        tvEmail    = findViewById(R.id.tvEmail);
        tvRolePill = findViewById(R.id.tvRolePill);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnEditAvatar).setOnClickListener(v ->
                Toast.makeText(this, "Photo upload coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        findViewById(R.id.rowChangePassword).setOnClickListener(v ->
                new ChangePasswordDialog().show(getSupportFragmentManager(), "change_password"));

        findViewById(R.id.rowPaymentMethods).setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowSavedAddresses).setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowLanguage).setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        SwitchMaterial switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowHelp).setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.rowTerms).setOnClickListener(v ->
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnSignOut).setOnClickListener(v -> confirmSignOut());

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadUser() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            showUnknownUser();
            return;
        }

        userRepository.fetchUser(currentUser.getUid(), new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    showUnknownUser();
                    return;
                }
                tvName.setText(user.getFullName() != null ? user.getFullName() : "Unknown");
                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                tvRolePill.setText(roleLabel(user.getRole()));
            }

            @Override
            public void onFailure(String message) {
                showUnknownUser();
            }
        });
    }

    private void showUnknownUser() {
        tvName.setText("Unknown");
        tvEmail.setText("");
        tvRolePill.setText("");
    }

    private String roleLabel(String role) {
        if (Constants.ROLE_HANDYMAN.equals(role)) return "Handyman";
        if (Constants.ROLE_CUSTOMER.equals(role)) return "Customer";
        return "";
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(this)
                .setTitle("Sign out?")
                .setMessage("You will need to log in again to access your account.")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authRepository.signOut();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) return true;
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, CustomerHomeActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            }
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
