package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Review;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviews = new ArrayList<>();
    private final Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void updateData(List<Review> newData) {
        reviews.clear();
        reviews.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvReviewerName.setText(review.getCustomerName() != null ? review.getCustomerName() : "");
        holder.tvReviewText.setText(review.getText() != null ? review.getText() : "");
        holder.tvReviewTime.setText(formatRelativeTime(review.getCreatedAt()));
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private String formatRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "";

        long diffMillis = System.currentTimeMillis() - timestamp.toDate().getTime();
        long minutes = diffMillis / (60 * 1000);
        long hours = diffMillis / (60 * 60 * 1000);

        Calendar today = Calendar.getInstance();
        Calendar reviewDay = Calendar.getInstance();
        reviewDay.setTime(timestamp.toDate());

        boolean sameDay = today.get(Calendar.YEAR) == reviewDay.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == reviewDay.get(Calendar.DAY_OF_YEAR);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = yesterday.get(Calendar.YEAR) == reviewDay.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == reviewDay.get(Calendar.DAY_OF_YEAR);

        if (sameDay) {
            if (hours >= 1) {
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (minutes >= 1) {
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else {
                return "Just now";
            }
        } else if (isYesterday) {
            return "Yesterday";
        } else {
            return new SimpleDateFormat("d MMM yyyy", Locale.UK).format(timestamp.toDate());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvReviewerName;
        final RatingBar ratingBar;
        final TextView tvReviewTime;
        final TextView tvReviewText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            ratingBar      = itemView.findViewById(R.id.ratingBarItem);
            tvReviewTime   = itemView.findViewById(R.id.tvReviewTime);
            tvReviewText   = itemView.findViewById(R.id.tvReviewText);
        }
    }
}
