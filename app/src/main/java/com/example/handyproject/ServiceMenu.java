package com.example.handyproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServiceMenu extends AppCompatActivity {

    private HandymanAdapter adapter;
    private final List<HandymanAdapter.Handyman> handymen = new ArrayList<>();
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_menu);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HandymanAdapter(handymen, this::showPopup);
        recyclerView.setAdapter(adapter);

        loadHandymen();
    }

    private void loadHandymen() {
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("role", "handyman")
                .limit(10)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("ServiceMenu", "Listen failed", error);
                        return;
                    }
                    if (snapshots == null) return;

                    handymen.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String fullName        = doc.getString("fullName");
                        String email           = doc.getString("email");
                        String serviceCategory = doc.getString("serviceCategory");
                        String location        = doc.getString("location");
                        Double hourlyRate      = doc.getDouble("hourlyRate");
                        Double rating          = doc.getDouble("rating");

                        handymen.add(new HandymanAdapter.Handyman(
                                fullName        != null ? fullName        : "",
                                email           != null ? email           : "",
                                serviceCategory != null ? serviceCategory : "",
                                hourlyRate      != null ? hourlyRate      : 0.0,
                                location        != null ? location        : "",
                                rating          != null ? rating          : 0.0
                        ));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    private void showPopup(String handymanName, String handymanEmail) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        TextView nameView = new TextView(this);
        nameView.setText(handymanName);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        TextView emailView = new TextView(this);
        emailView.setText(handymanEmail);
        emailView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        EditText bodyEditText = new EditText(this);
        bodyEditText.setHint("Message to include:");

        layout.addView(nameView);
        layout.addView(emailView);
        layout.addView(bodyEditText);

        new AlertDialog.Builder(this)
                .setView(layout)
                .setCustomTitle(buildCentredTitle("Compose Email"))
                .setPositiveButton("Send", (dialog, which) -> {
                    String prefix = "We are reaching out to you from the Handy Team with a new service request.\n\n";
                    String suffix = "\n\nThank you for being a valued member of our Handy community.\n\nBest regards,\nThe Handy Team.";
                    sendEmail(handymanEmail, prefix + bodyEditText.getText().toString() + suffix);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sendEmail(String recipientEmail, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Service Request From Handy");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
        requestNotification();
    }

    private View buildCentredTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 32, 0, 16);
        return title;
    }

    private void requestNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "app_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Service Request")
                .setContentText("Your request has been sent via Handy.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return;
        manager.notify(1, builder.build());
    }
}
