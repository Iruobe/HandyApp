package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.MessageRepository;
import com.example.handyproject.ui.common.adapters.ChatMessageAdapter;
import com.example.handyproject.ui.common.utils.ImageUtils;
import com.example.handyproject.utils.Constants;
import com.example.handyproject.utils.CurrencyUtils;
import com.google.firebase.auth.FirebaseUser;

import android.widget.TextView;

public class ChatThreadActivity extends AppCompatActivity
        implements ChatMessageAdapter.BookingActionListener {

    public static final String EXTRA_CONTACT_NAME   = "contact_name";
    public static final String EXTRA_CONVERSATION_ID = "conversation_id";

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private ChatMessageAdapter adapter;

    private MessageRepository messageRepository;
    private String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);

        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        if (conversationId == null) {
            finish();
            return;
        }

        AuthRepository authRepository = new AuthRepository();
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        messageRepository = new MessageRepository();

        rvMessages     = findViewById(R.id.rvMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        TextView tvContactName = findViewById(R.id.tvContactName);
        View tvOnlineStatus    = findViewById(R.id.tvOnlineStatus);
        View viewOnlineDot     = findViewById(R.id.viewOnlineDot);

        ImageUtils.loadAvatar(findViewById(R.id.ivContactAvatar), null);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String contactName = getIntent().getStringExtra(EXTRA_CONTACT_NAME);
        tvContactName.setText(contactName != null ? contactName : "Chat");

        tvOnlineStatus.setVisibility(View.GONE);
        viewOnlineDot.setVisibility(View.GONE);

        setupRecyclerView(currentUser.getUid());

        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageRepository.startThreadListening(conversationId, new MessageRepository.ThreadMessageCallback() {
            @Override
            public void onUpdate(java.util.List<com.example.handyproject.data.model.Message> messages) {
                adapter.updateData(messages);
                if (!messages.isEmpty()) {
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatThreadActivity.this,
                        "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRepository.stopThreadListening();
    }

    private void setupRecyclerView(String currentUid) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        adapter = new ChatMessageAdapter(currentUid, this);
        rvMessages.setAdapter(adapter);
    }

    private void sendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        messageRepository.sendMessage(conversationId, text, new MessageRepository.MessageSendCallback() {
            @Override
            public void onSuccess() {
                etMessageInput.setText("");
                // Snapshot listener echoes the new message back — no manual list update needed
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ChatThreadActivity.this,
                        "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfirm(String messageId, double quoteAmount) {
        messageRepository.updateBookingStatus(
                conversationId, messageId, Constants.BOOKING_STATUS_CONFIRMED, quoteAmount,
                new MessageRepository.MessageSendCallback() {
                    @Override
                    public void onSuccess() {
                        String outcomeText = "Booking confirmed — "
                                + CurrencyUtils.formatAmount(quoteAmount);
                        messageRepository.sendMessage(conversationId, outcomeText,
                                new MessageRepository.MessageSendCallback() {
                                    @Override public void onSuccess() {}
                                    @Override public void onError(String msg) {
                                        Toast.makeText(ChatThreadActivity.this,
                                                "Booking confirmed but failed to notify",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(ChatThreadActivity.this,
                                "Failed to confirm booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeny(String messageId) {
        messageRepository.updateBookingStatus(
                conversationId, messageId, Constants.BOOKING_STATUS_DENIED, null,
                new MessageRepository.MessageSendCallback() {
                    @Override
                    public void onSuccess() {
                        messageRepository.sendMessage(conversationId, "Booking denied",
                                new MessageRepository.MessageSendCallback() {
                                    @Override public void onSuccess() {}
                                    @Override public void onError(String msg) {
                                        Toast.makeText(ChatThreadActivity.this,
                                                "Booking denied but failed to notify",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(ChatThreadActivity.this,
                                "Failed to deny booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
