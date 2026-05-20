package com.example.handyproject.ui.common.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.utils.CurrencyUtils;

import java.util.List;
import java.util.Locale;

public class HandymanAdapter extends RecyclerView.Adapter<HandymanAdapter.ViewHolder> {

    public interface OnHandymanClickListener {
        void onHandymanClick(String fullName, String email);
    }

    private final List<Handyman> handymen;
    private final OnHandymanClickListener listener;

    public HandymanAdapter(List<Handyman> handymen, OnHandymanClickListener listener) {
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
        holder.nameText.setText(h.getFullName());
        holder.categoryText.setText(h.getServiceCategory());
        holder.rateText.setText(CurrencyUtils.formatRate(h.getHourlyRate()));
        holder.locationText.setText(h.getLocation());
        holder.ratingText.setText(String.format(Locale.UK, "★ %.1f", h.getRating()));
        holder.itemView.setOnClickListener(v ->
                listener.onHandymanClick(h.getFullName(), h.getEmail()));
    }

    @Override
    public int getItemCount() { return handymen.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
