package com.example.handyproject.ui.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Review;
import com.example.handyproject.data.model.User;
import com.example.handyproject.data.repository.ReviewRepository;
import com.example.handyproject.data.repository.UserRepository;
import com.example.handyproject.ui.common.adapters.ReviewAdapter;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class ProfileReviewsFragment extends Fragment
        implements LeaveReviewDialog.OnReviewSubmittedListener {

    private static final String ARG_HANDYMAN_UID = "handyman_uid";
    private static final String ARG_CURRENT_UID  = "current_uid";

    private final ReviewRepository reviewRepository = new ReviewRepository();
    private final UserRepository userRepository     = new UserRepository();

    private String handymanUid;
    private String currentUid;
    private String customerName = "Customer";

    private int existingRating = 0;
    private String existingText = "";

    private MaterialButton btnLeaveReview;
    private TextView tvAverageRating;
    private RatingBar ratingBarSummary;
    private TextView tvReviewCount;
    private RecyclerView rvReviews;
    private TextView tvEmptyReviews;
    private ReviewAdapter adapter;

    public static ProfileReviewsFragment newInstance(String handymanUid, String currentUid) {
        ProfileReviewsFragment fragment = new ProfileReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HANDYMAN_UID, handymanUid);
        args.putString(ARG_CURRENT_UID, currentUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        handymanUid = args != null ? args.getString(ARG_HANDYMAN_UID) : null;
        currentUid  = args != null ? args.getString(ARG_CURRENT_UID)  : null;

        btnLeaveReview   = view.findViewById(R.id.btnLeaveReview);
        tvAverageRating  = view.findViewById(R.id.tvAverageRating);
        ratingBarSummary = view.findViewById(R.id.ratingBarSummary);
        tvReviewCount    = view.findViewById(R.id.tvReviewCount);
        rvReviews        = view.findViewById(R.id.rvReviews);
        tvEmptyReviews   = view.findViewById(R.id.tvEmptyReviews);

        adapter = new ReviewAdapter(requireContext());
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReviews.setAdapter(adapter);

        btnLeaveReview.setOnClickListener(v -> openReviewDialog());

        if (handymanUid != null) {
            loadReviews();
        }

        if (currentUid != null) {
            fetchCustomerName();
            checkEarnedGate();
        }
    }

    private void fetchCustomerName() {
        userRepository.fetchUser(currentUid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getFullName() != null && !user.getFullName().isEmpty()) {
                    customerName = user.getFullName();
                }
            }

            @Override
            public void onFailure(String message) {
                // keep default "Customer"
            }
        });
    }

    private void loadReviews() {
        reviewRepository.getReviewsForHandyman(handymanUid, new ReviewRepository.ReviewListCallback() {
            @Override
            public void onSuccess(List<Review> reviews) {
                if (!isAdded()) return;
                adapter.updateData(reviews);
                updateSummary(reviews);
                boolean empty = reviews.isEmpty();
                rvReviews.setVisibility(empty ? View.GONE : View.VISIBLE);
                tvEmptyReviews.setVisibility(empty ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummary(List<Review> reviews) {
        if (reviews.isEmpty()) {
            tvAverageRating.setText("–");
            ratingBarSummary.setRating(0);
            tvReviewCount.setText("No reviews yet");
            return;
        }
        double sum = 0;
        for (Review r : reviews) sum += r.getRating();
        float avg = (float) (sum / reviews.size());
        tvAverageRating.setText(String.format(Locale.UK, "%.1f", avg));
        ratingBarSummary.setRating(avg);
        int count = reviews.size();
        tvReviewCount.setText(count + (count == 1 ? " review" : " reviews"));
    }

    private void checkEarnedGate() {
        String role = requireContext()
                .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                .getString(Constants.PREF_KEY_ROLE, "");

        if (!Constants.ROLE_CUSTOMER.equals(role)) return;

        reviewRepository.hasConfirmedBooking(currentUid, handymanUid,
                new ReviewRepository.EarnedGateCallback() {
                    @Override
                    public void onResult(boolean hasConfirmedBooking) {
                        if (!isAdded() || !hasConfirmedBooking) return;
                        loadExistingReviewForButton();
                    }

                    @Override
                    public void onError(String message) {
                        // fail closed — keep button hidden
                    }
                });
    }

    private void loadExistingReviewForButton() {
        reviewRepository.getExistingReview(handymanUid, currentUid,
                new ReviewRepository.SingleReviewCallback() {
                    @Override
                    public void onSuccess(Review review) {
                        if (!isAdded()) return;
                        if (review != null) {
                            existingRating = review.getRating();
                            existingText   = review.getText() != null ? review.getText() : "";
                            btnLeaveReview.setText("Edit Your Review");
                        }
                        btnLeaveReview.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        btnLeaveReview.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void openReviewDialog() {
        LeaveReviewDialog dialog = LeaveReviewDialog.newInstance(
                handymanUid, currentUid, customerName, existingRating, existingText);
        dialog.show(getChildFragmentManager(), "leave_review");
    }

    @Override
    public void onReviewSubmitted() {
        existingRating = 0;
        existingText   = "";
        btnLeaveReview.setText("Edit Your Review");
        loadReviews();
    }
}
