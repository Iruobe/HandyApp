package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Notification;
import com.example.handyproject.utils.Constants;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    private final List<Notification> notifications = new ArrayList<>();
    private final Context context;
    private final OnNotificationClickListener listener;

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<Notification> newData) {
        notifications.clear();
        notifications.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvBody.setText(notification.getBody());
        holder.tvTime.setText(formatRelativeTime(notification.getCreatedAt()));
        holder.viewUnreadDot.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        applyTypeStyle(holder, notification.getType());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void applyTypeStyle(ViewHolder holder, String type) {
        int iconRes;
        int colorRes;

        if (Constants.TYPE_MESSAGE.equals(type)) {
            iconRes = R.drawable.ic_message;
            colorRes = R.color.colorSuccess;
        } else if (Constants.TYPE_SYSTEM.equals(type)) {
            iconRes = R.drawable.ic_notifications;
            colorRes = R.color.colorWarning;
        } else {
            iconRes = R.drawable.ic_calendar;
            colorRes = R.color.colorPrimary;
        }

        holder.ivIcon.setImageResource(iconRes);
        ImageViewCompat.setImageTintList(holder.ivIcon,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
        ViewCompat.setBackgroundTintList(holder.iconContainer,
                ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)));
    }

    private String formatRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "";

        long diffMillis = System.currentTimeMillis() - timestamp.toDate().getTime();
        long minutes = diffMillis / (60 * 1000);
        long hours = diffMillis / (60 * 60 * 1000);

        Calendar today = Calendar.getInstance();
        Calendar notifDay = Calendar.getInstance();
        notifDay.setTime(timestamp.toDate());

        boolean sameDay = today.get(Calendar.YEAR) == notifDay.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == notifDay.get(Calendar.DAY_OF_YEAR);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = yesterday.get(Calendar.YEAR) == notifDay.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == notifDay.get(Calendar.DAY_OF_YEAR);

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
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.UK);
            return sdf.format(timestamp.toDate());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout iconContainer;
        final ImageView ivIcon;
        final TextView tvTitle;
        final TextView tvBody;
        final TextView tvTime;
        final View viewUnreadDot;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            ivIcon        = itemView.findViewById(R.id.ivIcon);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvBody        = itemView.findViewById(R.id.tvBody);
            tvTime        = itemView.findViewById(R.id.tvTime);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}
