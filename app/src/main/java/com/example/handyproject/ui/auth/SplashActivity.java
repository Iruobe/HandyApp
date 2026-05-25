package com.example.handyproject.ui.auth;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.remote.FirebaseService;
import com.example.handyproject.ui.customer.ServiceMenu;
import com.example.handyproject.utils.Constants;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_splash);

        makeFullScreen();
        applyGradient();
        animateViews();

        new Handler(Looper.getMainLooper()).postDelayed(this::navigate, 2500);
    }

    private void makeFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.statusBars()
                | WindowInsetsCompat.Type.navigationBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void applyGradient() {
        ConstraintLayout root = findViewById(R.id.splashRoot);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, R.color.colorPrimary),
                        ContextCompat.getColor(this, R.color.colorPrimaryDark)
                });
        root.setBackground(gradient);
    }

    private void animateViews() {
        android.view.View logo = findViewById(R.id.splashLogo);
        TextView appName       = findViewById(R.id.splashAppName);
        TextView tagline       = findViewById(R.id.splashTagline);

        logo.animate().alpha(1f).setDuration(600).withEndAction(() ->
                appName.animate().alpha(1f).setDuration(200).withEndAction(() ->
                        tagline.animate().alpha(0.85f).setDuration(200).start()
                ).start()
        ).start();
    }

    private void navigate() {
        FirebaseUser user = FirebaseService.getAuth().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        FirebaseService.getFirestore()
                .collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString(Constants.FIELD_ROLE);
                    Intent intent;
                    if (Constants.ROLE_HANDYMAN.equals(role)) {
                        // TODO: Replace with HandymanHomeActivity in Phase 2
                        intent = new Intent(this, ServiceMenu.class);
                    } else {
                        // TODO: Replace with CustomerHomeActivity in Phase 2
                        intent = new Intent(this, ServiceMenu.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
    }
}
