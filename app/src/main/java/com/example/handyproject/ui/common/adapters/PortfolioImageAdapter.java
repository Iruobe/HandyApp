package com.example.handyproject.ui.common.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class PortfolioImageAdapter extends RecyclerView.Adapter<PortfolioImageAdapter.ViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    private final List<String> imageUrls = new ArrayList<>();
    private final OnImageClickListener listener;

    public PortfolioImageAdapter(OnImageClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<String> newData) {
        imageUrls.clear();
        imageUrls.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_portfolio_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        ImageUtils.loadImage(holder.ivPortfolioImage, url);
        holder.itemView.setOnClickListener(v -> listener.onImageClick(url));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPortfolioImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPortfolioImage = itemView.findViewById(R.id.ivPortfolioImage);
        }
    }
}
