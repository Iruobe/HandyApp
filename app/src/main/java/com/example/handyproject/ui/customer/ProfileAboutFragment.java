package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.handyproject.R;
import com.example.handyproject.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ProfileAboutFragment extends Fragment {

    private static final String ARG_BIO = "bio";
    private static final String ARG_RESPONSE_TIME = "response_time";
    private static final String ARG_YEARS_EXPERIENCE = "years_experience";
    private static final String ARG_SERVICES_OFFERED = "services_offered";

    public static ProfileAboutFragment newInstance(String bio, String responseTime,
                                                     int yearsOfExperience, List<String> servicesOffered) {
        ProfileAboutFragment fragment = new ProfileAboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BIO, bio);
        args.putString(ARG_RESPONSE_TIME, responseTime);
        args.putInt(ARG_YEARS_EXPERIENCE, yearsOfExperience);
        args.putStringArrayList(ARG_SERVICES_OFFERED, new ArrayList<>(servicesOffered));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_about, container, false);

        Bundle args = getArguments();
        String bio = args != null ? args.getString(ARG_BIO) : null;
        String responseTime = args != null ? args.getString(ARG_RESPONSE_TIME) : null;
        int yearsOfExperience = args != null ? args.getInt(ARG_YEARS_EXPERIENCE) : 0;
        List<String> servicesOffered = args != null ? args.getStringArrayList(ARG_SERVICES_OFFERED) : null;

        TextView tvBio = view.findViewById(R.id.tvBio);
        tvBio.setText(bio != null && !bio.isEmpty() ? bio : "No bio added yet.");

        TextView tvYearsExperience = view.findViewById(R.id.tvYearsExperience);
        tvYearsExperience.setText(yearsOfExperience + (yearsOfExperience == 1 ? " Year" : " Years"));

        TextView tvResponseTime = view.findViewById(R.id.tvResponseTime);
        tvResponseTime.setText(responseTime != null && !responseTime.isEmpty()
                ? responseTime : Constants.DEFAULT_RESPONSE_TIME);

        View cardServicesOffered = view.findViewById(R.id.cardServicesOffered);
        LinearLayout llServicesOfferedContainer = view.findViewById(R.id.llServicesOfferedContainer);
        if (servicesOffered == null || servicesOffered.isEmpty()) {
            cardServicesOffered.setVisibility(View.GONE);
        } else {
            for (String service : servicesOffered) {
                View row = inflater.inflate(R.layout.item_service_offered_row, llServicesOfferedContainer, false);
                TextView tvServiceName = row.findViewById(R.id.tvServiceName);
                tvServiceName.setText(service);
                llServicesOfferedContainer.addView(row);
            }
        }

        return view;
    }
}
