package com.example.handyproject.ui.common.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.handyproject.R;
import com.example.handyproject.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ServicesInputHelper {

    private final Context context;
    private final LinearLayout container;
    private final MaterialButton btnAddService;
    private final List<TextInputLayout> rows = new ArrayList<>();

    public ServicesInputHelper(Context context, LinearLayout container, MaterialButton btnAddService) {
        this.context = context;
        this.container = container;
        this.btnAddService = btnAddService;
        btnAddService.setOnClickListener(v -> addRow(null));
    }

    public void addRow(String prefillText) {
        if (rows.size() >= Constants.MAX_SERVICES) return;

        View rowView = LayoutInflater.from(context)
                .inflate(R.layout.item_service_input_row, container, false);
        TextInputLayout til = rowView.findViewById(R.id.tilService);
        ImageButton btnRemove = rowView.findViewById(R.id.btnRemoveService);

        if (prefillText != null && til.getEditText() != null) {
            til.getEditText().setText(prefillText);
        }

        btnRemove.setOnClickListener(v -> removeRow(rowView, til));

        container.addView(rowView);
        rows.add(til);
        updateControls();
    }

    private void removeRow(View rowView, TextInputLayout til) {
        if (rows.size() <= 1) return;
        container.removeView(rowView);
        rows.remove(til);
        updateControls();
    }

    private void updateControls() {
        btnAddService.setVisibility(rows.size() >= Constants.MAX_SERVICES ? View.GONE : View.VISIBLE);
        boolean showRemove = rows.size() > 1;
        for (TextInputLayout til : rows) {
            View row = (View) til.getParent();
            ImageButton remove = row.findViewById(R.id.btnRemoveService);
            remove.setVisibility(showRemove ? View.VISIBLE : View.GONE);
        }
    }

    public List<String> getServices() {
        List<String> result = new ArrayList<>();
        for (TextInputLayout til : rows) {
            if (til.getEditText() != null) {
                String text = til.getEditText().getText().toString().trim();
                if (!text.isEmpty()) result.add(text);
            }
        }
        return result;
    }

    public void loadServices(List<String> existing) {
        container.removeAllViews();
        rows.clear();
        if (existing == null || existing.isEmpty()) {
            addRow(null);
        } else {
            for (String service : existing) {
                addRow(service);
            }
        }
    }
}
