package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.View;
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

    private static final int DATE_STRIP_DAYS = 60;

    private LinearLayout layoutDateChips;
    private LinearLayout layoutTimeChips;
    private android.widget.HorizontalScrollView scrollDateChips;
    private TextView tvMonthYear;
    private TextView tvProviderName;
    private TextView tvProviderService;
    private TextView tvProviderRating;
    private TextView tvHourlyRate;

    private int selectedDateIndex = 0;
    private int selectedTimeIndex = 0;

    private final String[] timeSlots = buildTimeSlots();

    private final MaterialCardView[] dateCards = new MaterialCardView[DATE_STRIP_DAYS];
    private final Calendar[] dateChipDates = new Calendar[DATE_STRIP_DAYS];
    private final MaterialCardView[] timeCards = new MaterialCardView[timeSlots.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        layoutDateChips   = findViewById(R.id.layoutDateChips);
        layoutTimeChips   = findViewById(R.id.layoutTimeChips);
        scrollDateChips   = findViewById(R.id.scrollDateChips);
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

        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> {
            Toast.makeText(this,
                    "Booking confirmed! A confirmation will be sent shortly.",
                    Toast.LENGTH_LONG).show();
            finish();
        });

        buildDateChips();
        buildTimeChips();
        setupDateScrollListener();
    }

    private void setupDateScrollListener() {
        int chipWidthPx = getResources().getDimensionPixelSize(R.dimen.date_chip_width);
        int marginEndPx = getResources().getDimensionPixelSize(R.dimen.padding_small);
        int stepPx = chipWidthPx + marginEndPx;

        scrollDateChips.setOnScrollChangeListener((View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            int index = scrollX / stepPx;
            if (index < 0) index = 0;
            if (index > DATE_STRIP_DAYS - 1) index = DATE_STRIP_DAYS - 1;
            updateMonthYearLabel(dateChipDates[index]);
        });
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

    private void updateMonthYearLabel(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(date.getTime()));
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

        updateMonthYearLabel(cal);

        for (int i = 0; i < DATE_STRIP_DAYS; i++) {
            final int index = i;
            final Calendar chipDate = (Calendar) cal.clone();

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
            inner.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.view.Gravity.CENTER));

            TextView tvDayName = new TextView(this);
            tvDayName.setText(dayNameFmt.format(cal.getTime()).toUpperCase(Locale.getDefault()));
            tvDayName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_caption));
            tvDayName.setGravity(android.view.Gravity.CENTER);
            tvDayName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView tvDayNumber = new TextView(this);
            tvDayNumber.setText(dayNumberFmt.format(cal.getTime()));
            tvDayNumber.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_size_subheading));
            tvDayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
            tvDayNumber.setGravity(android.view.Gravity.CENTER);
            tvDayNumber.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            inner.addView(tvDayName);
            inner.addView(tvDayNumber);
            card.addView(inner);

            applyDateChipStyle(card, tvDayName, tvDayNumber, index == selectedDateIndex);

            card.setOnClickListener(v -> {
                selectedDateIndex = index;
                updateMonthYearLabel(chipDate);
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
            dateChipDates[i] = chipDate;
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

            applyTimeChipStyle(card, tv, index == selectedTimeIndex);
            card.setOnClickListener(v -> {
                selectedTimeIndex = index;
                for (int j = 0; j < timeCards.length; j++) {
                    if (timeCards[j] == null) continue;
                    applyTimeChipStyle(
                            timeCards[j],
                            (TextView) timeCards[j].getChildAt(0),
                            j == selectedTimeIndex);
                }
            });

            timeCards[i] = card;
            layoutTimeChips.addView(card);
        }
    }

    // Fixed 8:00 AM-6:00 PM range. TODO: drive from real per-handyman
    // availability (set at registration/edit) in a future round.
    private static String[] buildTimeSlots() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);

        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a", Locale.getDefault());
        int slotCount = ((18 - 8) * 60 / 30) + 1;
        String[] slots = new String[slotCount];
        for (int i = 0; i < slotCount; i++) {
            slots[i] = timeFmt.format(cal.getTime());
            cal.add(Calendar.MINUTE, 30);
        }
        return slots;
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
