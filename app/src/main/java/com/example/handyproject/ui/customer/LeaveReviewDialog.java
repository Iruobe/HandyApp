package com.example.handyproject.ui.customer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Review;
import com.example.handyproject.data.repository.ReviewRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LeaveReviewDialog extends DialogFragment {

    public interface OnReviewSubmittedListener {
        void onReviewSubmitted();
    }

    private static final String ARG_HANDYMAN_ID      = "handyman_id";
    private static final String ARG_CURRENT_UID      = "current_uid";
    private static final String ARG_CUSTOMER_NAME    = "customer_name";
    private static final String ARG_EXISTING_RATING  = "existing_rating";
    private static final String ARG_EXISTING_TEXT    = "existing_text";

    private final ReviewRepository reviewRepository = new ReviewRepository();

    private RatingBar ratingBarInput;
    private TextInputEditText etReviewText;
    private MaterialButton btnSubmit;

    public static LeaveReviewDialog newInstance(String handymanId, String currentUid,
                                                String customerName, int existingRating,
                                                String existingText) {
        LeaveReviewDialog dialog = new LeaveReviewDialog();
        Bundle args = new Bundle();
        args.putString(ARG_HANDYMAN_ID, handymanId);
        args.putString(ARG_CURRENT_UID, currentUid);
        args.putString(ARG_CUSTOMER_NAME, customerName);
        args.putInt(ARG_EXISTING_RATING, existingRating);
        args.putString(ARG_EXISTING_TEXT, existingText != null ? existingText : "");
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_leave_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ratingBarInput = view.findViewById(R.id.ratingBarInput);
        etReviewText   = view.findViewById(R.id.etReviewText);
        btnSubmit      = view.findViewById(R.id.btnSubmit);

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> attemptSubmit());

        Bundle args = getArguments();
        if (args != null) {
            int existingRating = args.getInt(ARG_EXISTING_RATING, 0);
            String existingText = args.getString(ARG_EXISTING_TEXT, "");
            if (existingRating > 0) ratingBarInput.setRating(existingRating);
            if (existingText != null && !existingText.isEmpty()) etReviewText.setText(existingText);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void attemptSubmit() {
        Bundle args = getArguments();
        if (args == null) return;

        int rating = (int) ratingBarInput.getRating();
        String text = etReviewText.getText() != null
                ? etReviewText.getText().toString().trim() : "";

        if (rating < 1) {
            Toast.makeText(getContext(), "Please select a star rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);

        Review review = new Review();
        review.setHandymanId(args.getString(ARG_HANDYMAN_ID));
        review.setCustomerId(args.getString(ARG_CURRENT_UID));
        review.setCustomerName(args.getString(ARG_CUSTOMER_NAME, "Customer"));
        review.setRating(rating);
        review.setText(text);

        reviewRepository.submitReview(review, new ReviewRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                if (getParentFragment() instanceof OnReviewSubmittedListener) {
                    ((OnReviewSubmittedListener) getParentFragment()).onReviewSubmitted();
                }
                dismiss();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });
    }
}
