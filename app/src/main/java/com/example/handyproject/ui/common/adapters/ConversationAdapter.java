package com.example.handyproject.ui.common.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.customer.MessagesActivity.Conversation;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends
        RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> conversations = new ArrayList<>();
    private final Context context;
    private final OnConversationClickListener listener;

    public ConversationAdapter(Context context, OnConversationClickListener listener) {
        this.context = context;
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

        holder.tvName.setText(conversation.contactName);
        holder.tvTime.setText(conversation.timeLabel);
        holder.tvPreview.setText(conversation.lastMessage);

        holder.viewOnlineDot.setVisibility(conversation.online ? View.VISIBLE : View.GONE);
        ViewCompat.setBackgroundTintList(holder.viewOnlineDot,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorSuccess)));

        if (conversation.unreadCount > 0) {
            holder.tvUnreadBadge.setVisibility(View.VISIBLE);
            holder.tvUnreadBadge.setText(String.valueOf(conversation.unreadCount));
            ViewCompat.setBackgroundTintList(holder.tvUnreadBadge,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
            holder.tvPreview.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.tvPreview.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.tvUnreadBadge.setVisibility(View.GONE);
            holder.tvPreview.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));
            holder.tvPreview.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onConversationClick(conversation);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
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
