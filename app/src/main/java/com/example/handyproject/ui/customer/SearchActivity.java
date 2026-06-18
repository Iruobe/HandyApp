package com.example.handyproject.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.data.model.Handyman;
import com.example.handyproject.data.repository.HandymanRepository;
import com.example.handyproject.ui.common.adapters.SearchResultAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private SearchResultAdapter adapter;
    private final List<Handyman> handymen = new ArrayList<>();
    private HandymanRepository handymanRepository;
    private TextView tvResultsCount;
    private MaterialButton btnLoadMore;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvResultsCount  = findViewById(R.id.tvResultsCount);
        btnLoadMore     = findViewById(R.id.btnLoadMore);

        searchQuery = getIntent().getStringExtra("search_query");
        if (searchQuery == null) searchQuery = "";

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        adapter = new SearchResultAdapter(handymen, this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(adapter);

        setupBottomNav();
        loadHandymen();

        btnLoadMore.setOnClickListener(v ->
                Toast.makeText(this, "No more results", Toast.LENGTH_SHORT).show());
    }

    private void loadHandymen() {
        tvResultsCount.setText("Loading...");
        handymanRepository = new HandymanRepository();
        handymanRepository.startListening(new HandymanRepository.HandymanListCallback() {
            @Override
            public void onUpdate(List<Handyman> result) {
                handymen.clear();
                if (!searchQuery.isEmpty()) {
                    String query = searchQuery.toLowerCase();
                    for (Handyman h : result) {
                        boolean matchesCategory = h.getServiceCategory() != null
                                && h.getServiceCategory().toLowerCase().contains(query);
                        boolean matchesName = h.getFullName() != null
                                && h.getFullName().toLowerCase().contains(query);
                        if (matchesCategory || matchesName) {
                            handymen.add(h);
                        }
                    }
                } else {
                    handymen.addAll(result);
                }
                adapter.notifyDataSetChanged();
                int count = handymen.size();
                String label = count + " handyman" + (count == 1 ? "" : "s") + " found"
                        + (searchQuery.isEmpty() ? "" : " for \"" + searchQuery + "\"");
                tvResultsCount.setText(label);
                btnLoadMore.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                tvResultsCount.setText("Failed to load results");
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_search);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_search) return true;
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, CustomerHomeActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
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
    protected void onStop() {
        super.onStop();
        if (handymanRepository != null) handymanRepository.stopListening();
    }
}
