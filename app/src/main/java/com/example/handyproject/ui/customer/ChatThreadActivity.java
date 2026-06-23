package com.example.handyproject.ui.customer;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.adapters.ChatMessageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatThreadActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_NAME = "contact_name";

    public static class ChatMessage {
        public String text;
        public String timeLabel;
        public boolean isSent;

        public ChatMessage(String text, String timeLabel, boolean isSent) {
            this.text      = text;
            this.timeLabel = timeLabel;
            this.isSent    = isSent;
        }
    }

    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private ChatMessageAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);

        rvMessages     = findViewById(R.id.rvMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        TextView tvContactName   = findViewById(R.id.tvContactName);
        TextView tvOnlineStatus  = findViewById(R.id.tvOnlineStatus);
        View viewOnlineDot       = findViewById(R.id.viewOnlineDot);
        FrameLayout btnSend      = findViewById(R.id.btnSend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String contactName = getIntent().getStringExtra(EXTRA_CONTACT_NAME);
        tvContactName.setText(contactName != null ? contactName : "Chat");

        tvOnlineStatus.setText("Online");
        viewOnlineDot.setVisibility(View.VISIBLE);
        ViewCompat.setBackgroundTintList(viewOnlineDot,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorSuccess)));

        buildDummyThread();
        setupRecyclerView();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void buildDummyThread() {
        messages.add(new ChatMessage(
                "Hi! I saw your job posting for a leaky faucet repair.", "9:01 AM", false));
        messages.add(new ChatMessage(
                "Hi! Yes, it's been dripping in the kitchen for about a week now.", "9:03 AM", true));
        messages.add(new ChatMessage(
                "No problem, I can take a look. Are you available tomorrow afternoon?", "9:05 AM", false));
        messages.add(new ChatMessage(
                "That works for me. Around 2pm?", "9:06 AM", true));
        messages.add(new ChatMessage(
                "Perfect, 2pm it is. I'll bring the parts I think I'll need.", "9:08 AM", false));
        messages.add(new ChatMessage(
                "Great, thank you! Should I do anything to prepare?", "9:10 AM", true));
        messages.add(new ChatMessage(
                "Just make sure the area under the sink is accessible. See you tomorrow!", "9:12 AM", false));
        messages.add(new ChatMessage(
                "Will do, see you then!", "9:13 AM", true));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        adapter = new ChatMessageAdapter();
        rvMessages.setAdapter(adapter);
        adapter.updateData(messages);
    }

    private void sendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.UK);
        messages.add(new ChatMessage(text, sdf.format(new Date()), true));
        adapter.updateData(messages);
        etMessageInput.setText("");
        rvMessages.scrollToPosition(messages.size() - 1);
    }
}
