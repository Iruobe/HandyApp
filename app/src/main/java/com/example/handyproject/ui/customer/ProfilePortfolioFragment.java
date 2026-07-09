package com.example.handyproject.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.adapters.PortfolioImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfilePortfolioFragment extends Fragment implements PortfolioImageAdapter.OnImageClickListener {

    private static final String ARG_PORTFOLIO_PHOTOS = "portfolio_photos";

    public static ProfilePortfolioFragment newInstance(List<String> portfolioPhotos) {
        ProfilePortfolioFragment fragment = new ProfilePortfolioFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PORTFOLIO_PHOTOS,
                portfolioPhotos != null ? new ArrayList<>(portfolioPhotos) : new ArrayList<>());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_portfolio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        List<String> portfolioPhotos = args != null ? args.getStringArrayList(ARG_PORTFOLIO_PHOTOS) : null;
        if (portfolioPhotos == null) portfolioPhotos = new ArrayList<>();

        RecyclerView rvPortfolio = view.findViewById(R.id.rvPortfolio);
        TextView tvEmptyPortfolio = view.findViewById(R.id.tvEmptyPortfolio);

        PortfolioImageAdapter adapter = new PortfolioImageAdapter(this);
        rvPortfolio.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPortfolio.setAdapter(adapter);
        adapter.updateData(portfolioPhotos);

        boolean empty = portfolioPhotos.isEmpty();
        rvPortfolio.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmptyPortfolio.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onImageClick(String imageUrl) {
        PortfolioImageViewerDialog.newInstance(imageUrl).show(getChildFragmentManager(), "portfolio_viewer");
    }
}
