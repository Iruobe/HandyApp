package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.ui.customer.HandymanProfileActivity;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SearchResultAdapter extends
        RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final List<Handyman> handymen;
    private final Context context;

    public SearchResultAdapter(List<Handyman> handymen, Context context) {
        this.handymen = handymen;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Handyman handyman = handymen.get(position);

        ImageUtils.loadImage(holder.ivHandymanPhoto, null);
        holder.tvName.setText(handyman.getFullName());
        holder.tvServiceTitle.setText(handyman.getServiceCategory());
        holder.tvRating.setText(String.valueOf(handyman.getRating()));
        holder.tvReviewCount.setText("(0 reviews)");
        holder.tvTag1.setText(handyman.getServiceCategory());
        holder.tvTag2.setText("Available");
        holder.tvRate.setText(CurrencyUtils.formatRate(handyman.getHourlyRate()));
        holder.tvDistance.setText("Nearby");

        String description = handyman.getServiceDescription();
        if (description == null || description.trim().isEmpty()) {
            description = "Experienced professional ready to help with your needs.";
        }
        holder.tvDescription.setText(description);

        holder.ivVerified.setVisibility(
                handyman.isAvailableForHire() ? View.VISIBLE : View.GONE);

        holder.btnViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, HandymanProfileActivity.class);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HandymanProfileActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return handymen.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivHandymanPhoto;
        final TextView tvName;
        final TextView tvServiceTitle;
        final TextView tvRating;
        final TextView tvReviewCount;
        final TextView tvTag1;
        final TextView tvTag2;
        final TextView tvDescription;
        final TextView tvRate;
        final TextView tvDistance;
        final ImageView ivVerified;
        final MaterialButton btnViewProfile;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHandymanPhoto = itemView.findViewById(R.id.ivHandymanPhoto);
            tvName         = itemView.findViewById(R.id.tvName);
            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvRating       = itemView.findViewById(R.id.tvRating);
            tvReviewCount  = itemView.findViewById(R.id.tvReviewCount);
            tvTag1         = itemView.findViewById(R.id.tvTag1);
            tvTag2         = itemView.findViewById(R.id.tvTag2);
            tvDescription  = itemView.findViewById(R.id.tvDescription);
            tvRate         = itemView.findViewById(R.id.tvRate);
            tvDistance     = itemView.findViewById(R.id.tvDistance);
            ivVerified     = itemView.findViewById(R.id.ivVerified);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
        }
    }
}
