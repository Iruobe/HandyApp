package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.adapters.ConversationAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    public static class Conversation {
        public String contactName;
        public String lastMessage;
        public String timeLabel;
        public int unreadCount;
        public boolean online;

        public Conversation(String contactName, String lastMessage, String timeLabel,
                             int unreadCount, boolean online) {
            this.contactName = contactName;
            this.lastMessage = lastMessage;
            this.timeLabel   = timeLabel;
            this.unreadCount = unreadCount;
            this.online      = online;
        }
    }

    private RecyclerView rvConversations;
    private View layoutEmptyState;
    private ConversationAdapter adapter;
    private final List<Conversation> conversations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        rvConversations  = findViewById(R.id.rvConversations);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        findViewById(R.id.btnSearch).setOnClickListener(v ->
                Toast.makeText(this, "Search coming soon", Toast.LENGTH_SHORT).show());

        setupConversations();
        setupRecyclerView();
        setupBottomNav();
    }

    private void setupConversations() {
        conversations.add(new Conversation(
                "Marcus Rodriguez", "Sounds good, see you at 2pm!", "10:42 AM", 2, true));
        conversations.add(new Conversation(
                "Sarah Chen", "Thanks for the quick turnaround.", "9:15 AM", 0, true));
        conversations.add(new Conversation(
                "David Wilson", "Can you send a few more photos of the job?", "Yesterday", 1, false));
        conversations.add(new Conversation(
                "Julian Thorne", "I'll be there in 10 minutes.", "Yesterday", 0, false));
        conversations.add(new Conversation(
                "Elena Vance", "Perfect, that works for me.", "Mon", 0, true));
        conversations.add(new Conversation(
                "Michael Scott", "Invoice has been sent, let me know if you have questions.", "Mon", 0, false));

        toggleEmptyState();
    }

    private void setupRecyclerView() {
        adapter = new ConversationAdapter(this, conversation -> {
            Intent intent = new Intent(this, ChatThreadActivity.class);
            intent.putExtra(ChatThreadActivity.EXTRA_CONTACT_NAME, conversation.contactName);
            startActivity(intent);
        });
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(adapter);
        adapter.updateData(conversations);
    }

    private void toggleEmptyState() {
        boolean isEmpty = conversations.isEmpty();
        rvConversations.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_messages);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_messages) return true;
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, CustomerHomeActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
