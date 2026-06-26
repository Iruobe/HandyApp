package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Conversation;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConversationAdapter extends
        RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> conversations = new ArrayList<>();
    private final Context context;
    private final String currentUid;
    private final OnConversationClickListener listener;

    public ConversationAdapter(Context context, String currentUid, OnConversationClickListener listener) {
        this.context = context;
        this.currentUid = currentUid;
        this.listener = listener;
    }

    public void updateData(List<Conversation> newData) {
        conversations.clear();
        conversations.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        ImageUtils.loadAvatar(holder.ivAvatar, null);
        holder.tvName.setText(resolveOtherParticipantName(conversation, currentUid));
        holder.tvTime.setText(formatRelativeTime(conversation.getLastMessageTimestamp()));
        holder.tvPreview.setText(conversation.getLastMessage() != null ? conversation.getLastMessage() : "");

        // No presence system exists yet — always hidden rather than faked.
        holder.viewOnlineDot.setVisibility(View.GONE);
        // No per-user unread tracking exists yet — always hidden rather than faked.
        holder.tvUnreadBadge.setVisibility(View.GONE);
        holder.tvPreview.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));
        holder.tvPreview.setTypeface(null, android.graphics.Typeface.NORMAL);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onConversationClick(conversation);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static String resolveOtherParticipantName(Conversation conversation, String currentUid) {
        List<String> participantIds = conversation.getParticipantIds();
        Map<String, String> participantNames = conversation.getParticipantNames();
        if (participantIds == null || participantNames == null) return "";

        for (String uid : participantIds) {
            if (!uid.equals(currentUid)) {
                String name = participantNames.get(uid);
                return name != null ? name : "";
            }
        }
        return "";
    }

    private String formatRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "";

        long diffMillis = System.currentTimeMillis() - timestamp.toDate().getTime();
        long minutes = diffMillis / (60 * 1000);
        long hours = diffMillis / (60 * 60 * 1000);

        Calendar today = Calendar.getInstance();
        Calendar messageDay = Calendar.getInstance();
        messageDay.setTime(timestamp.toDate());

        boolean sameDay = today.get(Calendar.YEAR) == messageDay.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == messageDay.get(Calendar.DAY_OF_YEAR);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = yesterday.get(Calendar.YEAR) == messageDay.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == messageDay.get(Calendar.DAY_OF_YEAR);

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
        final ShapeableImageView ivAvatar;
        final View viewOnlineDot;
        final TextView tvName;
        final TextView tvTime;
        final TextView tvPreview;
        final TextView tvUnreadBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar      = itemView.findViewById(R.id.ivAvatar);
            viewOnlineDot = itemView.findViewById(R.id.viewOnlineDot);
            tvName        = itemView.findViewById(R.id.tvName);
            tvTime        = itemView.findViewById(R.id.tvTime);
            tvPreview     = itemView.findViewById(R.id.tvPreview);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }
    }
}
