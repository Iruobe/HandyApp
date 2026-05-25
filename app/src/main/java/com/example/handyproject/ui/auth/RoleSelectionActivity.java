package com.example.handyproject.ui.auth;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class RoleSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardCustomer;
    private MaterialCardView cardHandyman;
    private FrameLayout iconBgCustomer;
    private FrameLayout iconBgHandyman;
    private ImageView iconCustomer;
    private ImageView iconHandyman;
    private View radioInnerCustomer;
    private View radioInnerHandyman;

    private boolean isCustomerSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        cardCustomer       = findViewById(R.id.cardCustomer);
        cardHandyman       = findViewById(R.id.cardHandyman);
        iconBgCustomer     = findViewById(R.id.iconBgCustomer);
        iconBgHandyman     = findViewById(R.id.iconBgHandyman);
        iconCustomer       = findViewById(R.id.iconCustomer);
        iconHandyman       = findViewById(R.id.iconHandyman);
        radioInnerCustomer = findViewById(R.id.radioInnerCustomer);
        radioInnerHandyman = findViewById(R.id.radioInnerHandyman);

        ImageButton btnBack        = findViewById(R.id.btnBack);
        MaterialButton btnContinue = findViewById(R.id.btnContinue);

        updateCardStates();

        btnBack.setOnClickListener(v -> finish());

        cardCustomer.setOnClickListener(v -> {
            isCustomerSelected = true;
            updateCardStates();
        });

        cardHandyman.setOnClickListener(v -> {
            isCustomerSelected = false;
            updateCardStates();
        });

        btnContinue.setOnClickListener(v -> {
            Class<?> destination = isCustomerSelected
                    ? CustomerRegistration.class
                    : HandymanRegistration.class;
            startActivity(new Intent(this, destination));
        });
    }

    private void updateCardStates() {
        int strokePx = (int) (2 * getResources().getDisplayMetrics().density);

        int primaryColor      = ContextCompat.getColor(this, R.color.colorPrimary);
        int borderColor       = ContextCompat.getColor(this, R.color.colorUnselectedBorder);
        int unselectedBgColor = ContextCompat.getColor(this, R.color.colorUnselectedBackground);

        if (isCustomerSelected) {
            applySelected(cardCustomer, iconBgCustomer, iconCustomer,
                    radioInnerCustomer, primaryColor, strokePx);
            applyUnselected(cardHandyman, iconBgHandyman, iconHandyman,
                    radioInnerHandyman, borderColor, unselectedBgColor, strokePx);
        } else {
            applyUnselected(cardCustomer, iconBgCustomer, iconCustomer,
                    radioInnerCustomer, borderColor, unselectedBgColor, strokePx);
            applySelected(cardHandyman, iconBgHandyman, iconHandyman,
                    radioInnerHandyman, primaryColor, strokePx);
        }
    }

    private void applySelected(MaterialCardView card, FrameLayout iconBg,
            ImageView icon, View radioInner, int primaryColor, int strokePx) {
        card.setStrokeColor(primaryColor);
        card.setStrokeWidth(strokePx);

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setShape(GradientDrawable.OVAL);
        bgDrawable.setColor(primaryColor);
        iconBg.setBackground(bgDrawable);

        icon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        radioInner.setVisibility(View.VISIBLE);
    }

    private void applyUnselected(MaterialCardView card, FrameLayout iconBg,
            ImageView icon, View radioInner, int borderColor,
            int unselectedBgColor, int strokePx) {
        card.setStrokeColor(borderColor);
        card.setStrokeWidth(strokePx);

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setShape(GradientDrawable.OVAL);
        bgDrawable.setColor(unselectedBgColor);
        iconBg.setBackground(bgDrawable);

        icon.setColorFilter(ContextCompat.getColor(this, R.color.colorUnselectedIcon));
        radioInner.setVisibility(View.GONE);
    }
}
