package com.example.handyproject.ui.customer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.handyproject.R;
import com.example.handyproject.ui.common.utils.ImageUtils;

public class PortfolioImageViewerDialog extends DialogFragment {

    private static final String ARG_IMAGE_URL = "image_url";

    public static PortfolioImageViewerDialog newInstance(String imageUrl) {
        PortfolioImageViewerDialog dialog = new PortfolioImageViewerDialog();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_portfolio_image_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String imageUrl = args != null ? args.getString(ARG_IMAGE_URL) : null;

        ImageView ivFullScreenImage = view.findViewById(R.id.ivFullScreenImage);
        ImageUtils.loadImage(ivFullScreenImage, imageUrl);

        view.findViewById(R.id.rootViewer).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
