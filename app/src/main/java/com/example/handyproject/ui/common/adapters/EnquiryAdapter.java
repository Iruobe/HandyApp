package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.handyman.HandymanHomeActivity.EnquiryItem;

import java.util.List;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.ViewHolder> {

    private final List<EnquiryItem> enquiries;

    public EnquiryAdapter(List<EnquiryItem> enquiries) {
        this.enquiries = enquiries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_enquiry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(enquiries.get(position));
    }

    @Override
    public int getItemCount() {
        return enquiries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCustomerName;
        private final TextView tvMessage;
        private final TextView tvTime;
        private final TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvEnquiryCustomerName);
            tvMessage      = itemView.findViewById(R.id.tvEnquiryMessage);
            tvTime         = itemView.findViewById(R.id.tvEnquiryTime);
            tvStatus       = itemView.findViewById(R.id.tvEnquiryStatus);
        }

        void bind(EnquiryItem item) {
            Context context = itemView.getContext();
            tvCustomerName.setText(item.customerName);
            tvMessage.setText(item.message);
            tvTime.setText(item.timestamp);
            tvStatus.setText(item.status);

            int bgColor;
            int textColor;
            switch (item.status) {
                case "New":
                    bgColor   = ContextCompat.getColor(context, R.color.colorErrorLight);
                    textColor = ContextCompat.getColor(context, R.color.colorError);
                    break;
                case "Booked":
                    bgColor   = ContextCompat.getColor(context, R.color.colorBookedLight);
                    textColor = ContextCompat.getColor(context, R.color.colorPrimary);
                    break;
                default:
                    bgColor   = ContextCompat.getColor(context, R.color.colorUnselectedBackground);
                    textColor = ContextCompat.getColor(context, R.color.colorTextPrimary);
                    break;
            }

            GradientDrawable badge = new GradientDrawable();
            badge.setColor(bgColor);
            badge.setCornerRadius(context.getResources()
                    .getDimension(R.dimen.corner_radius_small));
            tvStatus.setBackground(badge);
            tvStatus.setTextColor(textColor);
        }
    }
}
