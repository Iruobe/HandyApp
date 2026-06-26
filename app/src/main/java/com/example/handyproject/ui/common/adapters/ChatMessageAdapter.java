package com.example.handyproject.ui.common.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Message;
import com.example.handyproject.utils.Constants;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface BookingActionListener {
        void onConfirm(String messageId, double quoteAmount);
        void onDeny(String messageId);
    }

    private static final int VIEW_TYPE_SENT     = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    private static final int VIEW_TYPE_BOOKING  = 2;

    private final String currentUid;
    private final BookingActionListener bookingActionListener;
    private final List<Message> messages = new ArrayList<>();

    public ChatMessageAdapter(String currentUid, BookingActionListener bookingActionListener) {
        this.currentUid = currentUid;
        this.bookingActionListener = bookingActionListener;
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
            bindBooking((BookingViewHolder) holder, msg);
        } else {
            TextViewHolder th = (TextViewHolder) holder;
            th.tvMessageText.setText(msg.getBody());
            th.tvMessageTime.setText(formatTime(msg.getSentAt()));
        }
    }

    private void bindBooking(BookingViewHolder bh, Message msg) {
        bh.tvBookingDateTime.setText(formatDateTime(msg.getBookingScheduledAt()));
        bh.tvBookingAddress.setText(msg.getBookingAddress() != null ? msg.getBookingAddress() : "");
        String notes = msg.getBookingNotes();
        if (notes != null && !notes.isEmpty()) {
            bh.tvBookingNotes.setText(notes);
            bh.tvBookingNotes.setVisibility(View.VISIBLE);
        } else {
            bh.tvBookingNotes.setVisibility(View.GONE);
        }
        bh.tvBookingTime.setText(formatTime(msg.getSentAt()));

        boolean isHandyman = !currentUid.equals(msg.getSenderId());
        String status = msg.getBookingStatus();
        boolean isPending   = status == null || Constants.BOOKING_STATUS_PENDING.equals(status);
        boolean isConfirmed = Constants.BOOKING_STATUS_CONFIRMED.equals(status);

        // Status badge
        if (isPending && isHandyman) {
            bh.tvBookingStatus.setVisibility(View.GONE);
        } else if (isPending) {
            bh.tvBookingStatus.setText("Pending");
            bh.tvBookingStatus.setTextColor(
                    ContextCompat.getColor(bh.itemView.getContext(), R.color.colorWarning));
            bh.tvBookingStatus.setVisibility(View.VISIBLE);
        } else if (isConfirmed) {
            bh.tvBookingStatus.setText("Confirmed");
            bh.tvBookingStatus.setTextColor(
                    ContextCompat.getColor(bh.itemView.getContext(), R.color.colorSuccess));
            bh.tvBookingStatus.setVisibility(View.VISIBLE);
        } else {
            bh.tvBookingStatus.setText("Denied");
            bh.tvBookingStatus.setTextColor(
                    ContextCompat.getColor(bh.itemView.getContext(), R.color.colorError));
            bh.tvBookingStatus.setVisibility(View.VISIBLE);
        }

        // Quote display (confirmed only)
        if (isConfirmed && msg.getBookingQuoteAmount() != null) {
            bh.tvBookingQuoteDisplay.setText(CurrencyUtils.formatAmount(msg.getBookingQuoteAmount()));
            bh.tvBookingQuoteDisplay.setVisibility(View.VISIBLE);
        } else {
            bh.tvBookingQuoteDisplay.setVisibility(View.GONE);
        }

        // Handyman action controls (pending + handyman only)
        if (isPending && isHandyman) {
            bh.layoutHandymanActions.setVisibility(View.VISIBLE);
            bh.etBookingQuote.setText("");
            bh.btnConfirmBooking.setOnClickListener(v -> {
                String quoteText = bh.etBookingQuote.getText() != null
                        ? bh.etBookingQuote.getText().toString().trim() : "";
                if (quoteText.isEmpty()) {
                    Toast.makeText(v.getContext(),
                            "Please enter a quote amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(quoteText);
                } catch (NumberFormatException e) {
                    Toast.makeText(v.getContext(),
                            "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount <= 0) {
                    Toast.makeText(v.getContext(),
                            "Quote must be greater than £0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookingActionListener != null) {
                    bookingActionListener.onConfirm(msg.getMessageId(), amount);
                }
            });
            bh.btnDenyBooking.setOnClickListener(v -> {
                if (bookingActionListener != null) {
                    bookingActionListener.onDeny(msg.getMessageId());
                }
            });
        } else {
            bh.layoutHandymanActions.setVisibility(View.GONE);
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
        final TextView tvBookingStatus;
        final TextView tvBookingQuoteDisplay;
        final LinearLayout layoutHandymanActions;
        final TextInputEditText etBookingQuote;
        final MaterialButton btnConfirmBooking;
        final MaterialButton btnDenyBooking;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingDateTime     = itemView.findViewById(R.id.tvBookingDateTime);
            tvBookingAddress      = itemView.findViewById(R.id.tvBookingAddress);
            tvBookingNotes        = itemView.findViewById(R.id.tvBookingNotes);
            tvBookingTime         = itemView.findViewById(R.id.tvBookingTime);
            tvBookingStatus       = itemView.findViewById(R.id.tvBookingStatus);
            tvBookingQuoteDisplay = itemView.findViewById(R.id.tvBookingQuoteDisplay);
            layoutHandymanActions = itemView.findViewById(R.id.layoutHandymanActions);
            etBookingQuote        = itemView.findViewById(R.id.etBookingQuote);
            btnConfirmBooking     = itemView.findViewById(R.id.btnConfirmBooking);
            btnDenyBooking        = itemView.findViewById(R.id.btnDenyBooking);
        }
    }
}
