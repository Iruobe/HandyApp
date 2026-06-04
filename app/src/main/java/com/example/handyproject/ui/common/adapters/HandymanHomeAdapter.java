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
import com.example.handyproject.ui.customer.HandymanProfileActivity;
import com.example.handyproject.utils.CurrencyUtils;

import java.util.List;

public class HandymanHomeAdapter extends RecyclerView.Adapter<HandymanHomeAdapter.ViewHolder> {

    private final Context context;
    private final List<Handyman> handymen;

    public HandymanHomeAdapter(Context context, List<Handyman> handymen) {
        this.context  = context;
        this.handymen = handymen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_handyman_card_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Handyman handyman = handymen.get(position);

        holder.ivHandymanPhoto.setImageResource(R.drawable.defaultprofile);
        holder.tvHandymanName.setText(handyman.getFullName());
        holder.tvServiceType.setText(handyman.getServiceCategory());
        holder.tvRating.setText(String.valueOf(handyman.getRating()));
        holder.tvRate.setText(CurrencyUtils.formatRate(handyman.getHourlyRate()));

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
        final TextView tvHandymanName;
        final TextView tvServiceType;
        final TextView tvRating;
        final TextView tvRate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHandymanPhoto = itemView.findViewById(R.id.ivHandymanPhoto);
            tvHandymanName  = itemView.findViewById(R.id.tvHandymanName);
            tvServiceType   = itemView.findViewById(R.id.tvServiceType);
            tvRating        = itemView.findViewById(R.id.tvRating);
            tvRate          = itemView.findViewById(R.id.tvRate);
        }
    }
}
