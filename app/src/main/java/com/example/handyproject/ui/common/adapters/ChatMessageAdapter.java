package com.example.handyproject.ui.common.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Message;
import com.example.handyproject.utils.Constants;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT     = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    private static final int VIEW_TYPE_BOOKING  = 2;

    private final String currentUid;
    private final List<Message> messages = new ArrayList<>();

    public ChatMessageAdapter(String currentUid) {
        this.currentUid = currentUid;
    }

    public void updateData(List<Message> newData) {
        messages.clear();
        messages.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        if (Constants.TYPE_BOOKING.equals(msg.getType())) return VIEW_TYPE_BOOKING;
        return currentUid.equals(msg.getSenderId()) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_BOOKING) {
            View view = inflater.inflate(R.layout.item_message_booking, parent, false);
            return new BookingViewHolder(view);
        }
        int layoutRes = (viewType == VIEW_TYPE_SENT)
                ? R.layout.item_message_sent
                : R.layout.item_message_received;
        View view = inflater.inflate(layoutRes, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (holder instanceof BookingViewHolder) {
            BookingViewHolder bh = (BookingViewHolder) holder;
            bh.tvBookingDateTime.setText(formatDateTime(msg.getBookingScheduledAt()));
            bh.tvBookingAddress.setText(
                    msg.getBookingAddress() != null ? msg.getBookingAddress() : "");
            String notes = msg.getBookingNotes();
            if (notes != null && !notes.isEmpty()) {
                bh.tvBookingNotes.setText(notes);
                bh.tvBookingNotes.setVisibility(View.VISIBLE);
            } else {
                bh.tvBookingNotes.setVisibility(View.GONE);
            }
            bh.tvBookingTime.setText(formatTime(msg.getSentAt()));
        } else {
            TextViewHolder th = (TextViewHolder) holder;
            th.tvMessageText.setText(msg.getBody());
            th.tvMessageTime.setText(formatTime(msg.getSentAt()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static String formatTime(Timestamp sentAt) {
        if (sentAt == null) return "";
        return new SimpleDateFormat("h:mm a", Locale.UK).format(sentAt.toDate());
    }

    private static String formatDateTime(Timestamp scheduledAt) {
        if (scheduledAt == null) return "";
        return new SimpleDateFormat("EEE d MMM, h:mm a", Locale.UK).format(scheduledAt.toDate());
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMessageText;
        final TextView tvMessageTime;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        final TextView tvBookingDateTime;
        final TextView tvBookingAddress;
        final TextView tvBookingNotes;
        final TextView tvBookingTime;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvBookingAddress  = itemView.findViewById(R.id.tvBookingAddress);
            tvBookingNotes    = itemView.findViewById(R.id.tvBookingNotes);
            tvBookingTime     = itemView.findViewById(R.id.tvBookingTime);
        }
    }
}
