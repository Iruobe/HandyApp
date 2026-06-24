package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private final HandymanRepository handymanRepository = new HandymanRepository();

    private LinearLayout layoutDateChips;
    private LinearLayout layoutTimeChips;
    private TextView tvMonthYear;
    private TextView tvProviderName;
    private TextView tvProviderService;
    private TextView tvProviderRating;
    private TextView tvHourlyRate;

    private int selectedDateIndex = 1;
    private int selectedTimeIndex = 1;

    private final String[] timeSlots = {"09:00 AM", "10:30 AM", "01:00 PM", "03:30 PM"};
    private final boolean[] timeDisabled = {false, false, false, true};

    private final MaterialCardView[] dateCards = new MaterialCardView[5];
    private final MaterialCardView[] timeCards = new MaterialCardView[4];

    private final Calendar baseCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        layoutDateChips   = findViewById(R.id.layoutDateChips);
        layoutTimeChips   = findViewById(R.id.layoutTimeChips);
        tvMonthYear       = findViewById(R.id.tvMonthYear);
        tvProviderName    = findViewById(R.id.tvProviderName);
        tvProviderService = findViewById(R.id.tvProviderService);
        tvProviderRating  = findViewById(R.id.tvProviderRating);
        tvHourlyRate      = findViewById(R.id.tvHourlyRate);

        ImageUtils.loadAvatar(findViewById(R.id.ivProviderPhoto), null);

        loadHandyman();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvUseCurrentLocation).setOnClickListener(v ->
                Toast.makeText(this, "Location detection coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            baseCalendar.add(Calendar.MONTH, -1);
            updateMonthYear();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            baseCalendar.add(Calendar.MONTH, 1);
            updateMonthYear();
        });

        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> {
            Toast.makeText(this,
                    "Booking confirmed! A confirmation will be sent shortly.",
                    Toast.LENGTH_LONG).show();
            finish();
        });

        updateMonthYear();
        buildDateChips();
        buildTimeChips();
    }

    private void loadHandyman() {
        String uid = getIntent().getStringExtra(HandymanProfileActivity.EXTRA_HANDYMAN_UID);
        if (uid == null) {
            failAndFinish();
            return;
        }

        handymanRepository.fetchHandyman(uid, new HandymanRepository.HandymanCallback() {
            @Override
            public void onSuccess(Handyman handyman) {
                if (handyman == null) {
                    failAndFinish();
                    return;
                }
                populateProvider(handyman);
            }

            @Override
            public void onError(String message) {
                failAndFinish();
            }
        });
    }

    private void populateProvider(Handyman handyman) {
        tvProviderName.setText(handyman.getFullName() != null ? handyman.getFullName() : "");
        tvProviderService.setText(handyman.getServiceCategory() != null ? handyman.getServiceCategory() : "");
        tvProviderRating.setText(handyman.getRating() > 0
                ? String.format(Locale.UK, "%.1f", handyman.getRating())
                : "Not rated");
        tvHourlyRate.setText(CurrencyUtils.formatRate(handyman.getHourlyRate()));
    }

    private void failAndFinish() {
        Toast.makeText(this, "Unable to load booking details", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(baseCalendar.getTime()));
    }

    private void buildDateChips() {
        layoutDateChips.removeAllViews();

        int chipWidthPx  = getResources().getDimensionPixelSize(R.dimen.date_chip_width);
        int chipHeightPx = getResources().getDimensionPixelSize(R.dimen.date_chip_height);
        int cornerPx     = getResources().getDimensionPixelSize(R.dimen.corner_radius);
        int marginEndPx  = getResources().getDimensionPixelSize(R.dimen.padding_small);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dayNameFmt   = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dayNumberFmt = new SimpleDateFormat("d",   Locale.getDefault());

        for (int i = 0; i < 5; i++) {
            final int index = i;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(chipWidthPx, chipHeightPx);
            cardParams.setMarginEnd(marginEndPx);
            card.setLayoutParams(cardParams);
            card.setRadius(cornerPx);
            card.setCardElevation(0f);
            card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.divider_height));

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setGravity(android.view.Gravity.CENTER);
            inner.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            TextView tvDayName = new TextView(this);
            tvDayName.setText(dayNameFmt.format(cal.getTime()).toUpperCase(Locale.getDefault()));
            tvDayName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_caption));

            TextView tvDayNumber = new TextView(this);
            tvDayNumber.setText(dayNumberFmt.format(cal.getTime()));
            tvDayNumber.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_subheading));
            tvDayNumber.setTypeface(null, android.graphics.Typeface.BOLD);

            inner.addView(tvDayName);
            inner.addView(tvDayNumber);
            card.addView(inner);

            applyDateChipStyle(card, tvDayName, tvDayNumber, index == selectedDateIndex);

            card.setOnClickListener(v -> {
                selectedDateIndex = index;
                for (int j = 0; j < dateCards.length; j++) {
                    if (dateCards[j] == null) continue;
                    LinearLayout innerLayout = (LinearLayout) dateCards[j].getChildAt(0);
                    applyDateChipStyle(
                            dateCards[j],
                            (TextView) innerLayout.getChildAt(0),
                            (TextView) innerLayout.getChildAt(1),
                            j == selectedDateIndex);
                }
            });

            dateCards[i] = card;
            layoutDateChips.addView(card);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void applyDateChipStyle(MaterialCardView card,
                                     TextView tvDayName, TextView tvDayNumber,
                                     boolean selected) {
        if (selected) {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorPrimary));
            card.setStrokeColor(
                    ContextCompat.getColor(this, R.color.colorPrimary));
            tvDayName.setTextColor(
                    ContextCompat.getColor(this, R.color.white));
            tvDayNumber.setTextColor(
                    ContextCompat.getColor(this, R.color.white));
        } else {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorCardBackground));
            card.setStrokeColor(
                    ContextCompat.getColor(this, R.color.colorUnselectedBorder));
            tvDayName.setTextColor(
                    ContextCompat.getColor(this, R.color.colorTextSecondary));
            tvDayNumber.setTextColor(
                    ContextCompat.getColor(this, R.color.colorTextPrimary));
        }
    }

    private void buildTimeChips() {
        layoutTimeChips.removeAllViews();

        int cornerPx    = getResources().getDimensionPixelSize(R.dimen.corner_radius);
        int marginEndPx = getResources().getDimensionPixelSize(R.dimen.padding_small);
        int padHPx      = getResources().getDimensionPixelSize(R.dimen.padding_small);
        int padVPx      = getResources().getDimensionPixelSize(R.dimen.padding_xsmall);

        for (int i = 0; i < timeSlots.length; i++) {
            final int index = i;

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMarginEnd(marginEndPx);
            card.setLayoutParams(cardParams);
            card.setRadius(cornerPx);
            card.setCardElevation(0f);
            card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.divider_height));

            TextView tv = new TextView(this);
            tv.setText(timeSlots[i]);
            tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_body));
            tv.setPadding(padHPx, padVPx, padHPx, padVPx);
            card.addView(tv);

            if (timeDisabled[i]) {
                card.setAlpha(0.4f);
                card.setClickable(false);
                applyTimeChipStyle(card, tv, false);
            } else {
                applyTimeChipStyle(card, tv, index == selectedTimeIndex);
                card.setOnClickListener(v -> {
                    selectedTimeIndex = index;
                    for (int j = 0; j < timeCards.length; j++) {
                        if (timeCards[j] == null || timeDisabled[j]) continue;
                        applyTimeChipStyle(
                                timeCards[j],
                                (TextView) timeCards[j].getChildAt(0),
                                j == selectedTimeIndex);
                    }
                });
            }

            timeCards[i] = card;
            layoutTimeChips.addView(card);
        }
    }

    private void applyTimeChipStyle(MaterialCardView card, TextView tv, boolean selected) {
        if (selected) {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorPrimary));
            card.setStrokeColor(
                    ContextCompat.getColor(this, R.color.colorPrimary));
            tv.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorCardBackground));
            card.setStrokeColor(
                    ContextCompat.getColor(this, R.color.colorUnselectedBorder));
            tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}
