package com.example.handyproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HandymanAdapter extends RecyclerView.Adapter<HandymanAdapter.ViewHolder> {

    public interface OnHandymanClickListener {
        void onHandymanClick(String fullName, String email);
    }

    static class Handyman {
        String fullName, email, serviceCategory, location;
        double hourlyRate, rating;

        Handyman(String fullName, String email, String serviceCategory,
                 double hourlyRate, String location, double rating) {
            this.fullName = fullName;
            this.email = email;
            this.serviceCategory = serviceCategory;
            this.hourlyRate = hourlyRate;
            this.location = location;
            this.rating = rating;
        }
    }

    private final List<Handyman> handymen;
    private final OnHandymanClickListener listener;

    HandymanAdapter(List<Handyman> handymen, OnHandymanClickListener listener) {
        this.handymen = handymen;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_handyman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Handyman h = handymen.get(position);
        holder.nameText.setText(h.fullName);
        holder.categoryText.setText(h.serviceCategory);
        holder.rateText.setText(String.format("£%.2f/hr", h.hourlyRate));
        holder.locationText.setText(h.location);
        holder.ratingText.setText(String.format("★ %.1f", h.rating));
        holder.itemView.setOnClickListener(v -> listener.onHandymanClick(h.fullName, h.email));
    }

    @Override
    public int getItemCount() { return handymen.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, rateText, locationText, ratingText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText     = itemView.findViewById(R.id.handymanName);
            categoryText = itemView.findViewById(R.id.handymanCategory);
            rateText     = itemView.findViewById(R.id.handymanRate);
            locationText = itemView.findViewById(R.id.handymanLocation);
            ratingText   = itemView.findViewById(R.id.handymanRating);
        }
    }
}
