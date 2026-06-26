package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Conversation;
import com.example.handyproject.data.repository.AuthRepository;
import com.example.handyproject.data.repository.MessageRepository;
import com.example.handyproject.ui.common.adapters.ConversationAdapter;
import com.example.handyproject.ui.common.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView rvConversations;
    private View layoutEmptyState;
    private ConversationAdapter adapter;
    private final List<Conversation> conversations = new ArrayList<>();
    private final MessageRepository messageRepository = new MessageRepository();
    private final AuthRepository authRepository = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        rvConversations  = findViewById(R.id.rvConversations);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        findViewById(R.id.btnSearch).setOnClickListener(v ->
                Toast.makeText(this, "Search coming soon", Toast.LENGTH_SHORT).show());

        setupRecyclerView();
        setupBottomNav();
    }

    private void setupRecyclerView() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        String currentUid = currentUser != null ? currentUser.getUid() : null;

        adapter = new ConversationAdapter(this, currentUid, conversation -> {
            Intent intent = new Intent(this, ChatThreadActivity.class);
            intent.putExtra(ChatThreadActivity.EXTRA_CONTACT_NAME,
                    ConversationAdapter.resolveOtherParticipantName(conversation, currentUid));
            intent.putExtra(ChatThreadActivity.EXTRA_CONVERSATION_ID, conversation.getId());
            startActivity(intent);
        });
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(adapter);
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
                NavigationUtils.goHome(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        messageRepository.startListening(new MessageRepository.ConversationListCallback() {
            @Override
            public void onUpdate(List<Conversation> updatedConversations) {
                conversations.clear();
                conversations.addAll(updatedConversations);
                adapter.updateData(conversations);
                toggleEmptyState();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MessagesActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRepository.stopListening();
    }
}
