package com.example.handyproject.ui.customer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
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
    private EditText etAddress;

    private static final long LOCATION_TIMEOUT_MS = 10000L;

    private ActivityResultLauncher<String> locationPermissionLauncher;

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
        etAddress         = findViewById(R.id.etAddress);

        ImageUtils.loadAvatar(findViewById(R.id.ivProviderPhoto), null);

        loadHandyman();

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        fetchCurrentLocation();
                    } else {
                        showLocationFailure("Location permission needed — please type your address");
                    }
                });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.tvUseCurrentLocation).setOnClickListener(v -> requestCurrentLocation());

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

    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showLocationFailure("Location permission needed — please type your address");
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            showLocationFailure("Couldn't get your location — please type your address");
            return;
        }

        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        LocationListener[] listenerHolder = new LocationListener[1];

        Runnable onTimeout = () -> {
            locationManager.removeUpdates(listenerHolder[0]);
            showLocationFailure("Couldn't get your location — please type your address");
        };

        listenerHolder[0] = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                timeoutHandler.removeCallbacks(onTimeout);
                locationManager.removeUpdates(this);
                reverseGeocodeAndFill(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestSingleUpdate(provider, listenerHolder[0], Looper.getMainLooper());
        timeoutHandler.postDelayed(onTimeout, LOCATION_TIMEOUT_MS);
    }

    private void reverseGeocodeAndFill(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1,
                    new Geocoder.GeocodeListener() {
                        @Override
                        public void onGeocode(List<Address> addresses) {
                            runOnUiThread(() -> applyGeocodedAddress(addresses));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> showLocationFailure(
                                    "Couldn't get your location — please type your address"));
                        }
                    });
        } else {
            try {
                @SuppressWarnings("deprecation")
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                applyGeocodedAddress(addresses);
            } catch (IOException e) {
                showLocationFailure("Couldn't get your location — please type your address");
            }
        }
    }

    private void applyGeocodedAddress(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            showLocationFailure("Couldn't get your location — please type your address");
            return;
        }

        Address address = addresses.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(address.getAddressLine(i));
        }

        if (sb.length() == 0) {
            showLocationFailure("Couldn't get your location — please type your address");
            return;
        }

        etAddress.setText(sb.toString());
    }

    private void showLocationFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
