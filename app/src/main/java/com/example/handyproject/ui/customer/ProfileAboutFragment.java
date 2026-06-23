package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.handyproject.R;
import com.example.handyproject.utils.Constants;

public class ProfileAboutFragment extends Fragment {

    private static final String ARG_BIO = "bio";
    private static final String ARG_RESPONSE_TIME = "response_time";
    private static final String ARG_YEARS_EXPERIENCE = "years_experience";

    public static ProfileAboutFragment newInstance(String bio, String responseTime, int yearsOfExperience) {
        ProfileAboutFragment fragment = new ProfileAboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BIO, bio);
        args.putString(ARG_RESPONSE_TIME, responseTime);
        args.putInt(ARG_YEARS_EXPERIENCE, yearsOfExperience);
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

        TextView tvBio = view.findViewById(R.id.tvBio);
        tvBio.setText(bio != null && !bio.isEmpty() ? bio : "No bio added yet.");

        TextView tvYearsExperience = view.findViewById(R.id.tvYearsExperience);
        tvYearsExperience.setText(yearsOfExperience + (yearsOfExperience == 1 ? " Year" : " Years"));

        TextView tvResponseTime = view.findViewById(R.id.tvResponseTime);
        tvResponseTime.setText(responseTime != null && !responseTime.isEmpty()
                ? responseTime : Constants.DEFAULT_RESPONSE_TIME);

        return view;
    }
}
