package com.example.handyproject;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.ExecutionException;

public class ServiceMenu extends AppCompatActivity {
    private FirebaseAuth mAuth; // mAuth //shared instance of the FirebaseAuth object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_menu);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get references to the ScrollView and LinearLayout
        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout containerLayout = findViewById(R.id.containerLayout);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(Build.VERSION.SDK_INT >= 33){
                if(ContextCompat.checkSelfPermission(ServiceMenu.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ServiceMenu.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
            }
            CharSequence name = "This is the notification channel name";
            String description = "This is the description channel name";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("app_channel", name, importance);
            channel.setDescription(description);
            //Register channel with system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

            db.collection("HandyMen").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //QuerySnapshot document = task.getResult();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fullname = document.getString("Full Name");
                            String Serviceprovided = document.getString("Service Provided");

                            Log.d("ServiceMenu", "DocumentSnapshot data: " + document.getData());
//                            String NameValue = document.getData().toString();

                            // Use ServiceMenu.this to reference the outer class context
                            TextView handymanNameTextView = new TextView(ServiceMenu.this);
                            handymanNameTextView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            handymanNameTextView.setText(fullname);
                            handymanNameTextView.setTextSize(24); // Set text size to 24sp
                            handymanNameTextView.setPadding(0, 10, 0, 10); // Add top margin

                             //Dynamically create and add DescriptionTextView
                            TextView descriptionTextView = new TextView(ServiceMenu.this);
                            descriptionTextView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    getResources().getDimensionPixelSize(R.dimen.description_text_height))); // Set height to 46dp
                            descriptionTextView.setText(Serviceprovided);
                            descriptionTextView.setPadding(0, 0, 0, 10); // Add bottom margin

                            // Add the TextView to the LinearLayout
                            containerLayout.addView(handymanNameTextView);
                            handymanNameTextView.setOnClickListener(new View.OnClickListener(){

                                String handymanNameTextViewInString= handymanNameTextView.getText().toString();
                                @Override
                                public void onClick(View v){
                                    //handleNotification(handymanNameTextViewInString);
                                    //HandymenEmailFromDatabase(handymanNameTextViewInString);

                                    showPopup(v,handymanNameTextViewInString);
                                }
                            });


                            // Add the DescriptionTextView to the LinearLayout
                            containerLayout.addView(descriptionTextView);
                        }
                    } else {
                        Log.d("ServiceMenu", "get failed with ", task.getException());
                    }

                }
            });
    }

    public void TextViewValue(View v){
        TextView clickedTextView = (TextView) v;
        String HandymanName = clickedTextView.getText().toString();

        // Now, you can use the textViewValue as needed
        Log.d("ServiceMenu", "Clicked TextView Value: " + HandymanName);
    }

    private void handleNotification(String name){
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,"app_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Service Request")
                .setContentText("Hello,"+ name + " You have a request from Handy")
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        notificationManager.notify(1, builder.build());
    }


    //Display dialog box when user clicks on handymen
    public void showPopup(View view, String HandymanNameDisplay) {
        // Create a layout to hold multiple EditText views
        LinearLayout layout = new LinearLayout(ServiceMenu.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create TextView views for name user clicked on to display
        TextView HandymanName = new TextView(ServiceMenu.this);
        HandymanName.setText(HandymanNameDisplay);
        HandymanName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);

        // Create TextView views for email to display
        TextView HandymanEmail = new TextView(ServiceMenu.this);
        //String Email =HandymenEmailFromDatabase(HandymanNameDisplay);
        RetrieveEmailAndUpdateTextView(HandymanNameDisplay,HandymanEmail);
        //HandymanEmail.setText(HandymenEmailFromDatabase(HandymanNameDisplay));
        HandymanEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);



        // Create EditText views for email, email body, and recipient
//        EditText emailEditText = new EditText(ServiceMenu.this);
//        emailEditText.setHint("Email:");

        EditText bodyEditText = new EditText(ServiceMenu.this);
        bodyEditText.setHint("Email Body:");

        //EditText recipientEditText = new EditText(ServiceMenu.this);
        //recipientEditText.setHint("Recipient Email:");

        // Add EditText views and textview to the layout
        layout.addView(HandymanName);
        layout.addView(HandymanEmail);
        //layout.addView(emailEditText);
        layout.addView(bodyEditText);
        //layout.addView(recipientEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setCustomTitle(CenterTitle("Enter Details"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve user input and store in the variables
                        String userEmail = HandymanEmail.getText().toString();
                        String emailBody = bodyEditText.getText().toString();
                        //String recipient = recipientEditText.getText().toString();

                        EmailClientGenerator(userEmail,emailBody);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//To close the dialog
                    }
                });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void EmailClientGenerator(String SenderEmail,String EmailBody){
        String EmailSend = SenderEmail;
        String EmailSubject = "Service Request From Handy";
        String Emailbody = EmailBody;

        // define Intent object with action attribute as ACTION_SEND
        Intent intent = new Intent(Intent.ACTION_SEND);

        // add three fields to intent using putExtra function
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EmailSend});
        intent.putExtra(Intent.EXTRA_SUBJECT, EmailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, Emailbody);

        // set type of intent
        intent.setType("message/rfc822");

        // startActivity with intent with chooser as Email client using createChooser function
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

    // Function to create a centered title for the AlertDialog
    private View CenterTitle(String titleText) {
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 20); // Adjust padding as needed

        return title;
    }

    public String HandymenEmailFromDatabase(String FullName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] Email = new String[1];
        db.collection("HandyMen").document(FullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                String email;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        email = document.getString("Email");
                        Email[0] = email;
                        Log.d("ServiceMenu", "DocumentSnapshot data: " + email);
                    }else{
                        Log.d("ServiceMenu", "No such document");
                    }
                } else {
                    Log.d("ServiceMenu", "get failed with ", task.getException());
                }

            }

        });
        return Email[0];
    }

    private void RetrieveEmailAndUpdateTextView(String fullName, TextView textView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("HandyMen").document(fullName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("Email");
                        Log.d("ServiceMenu", "DocumentSnapshot data: " + email);
                        textView.setText(email != null ? email : "Email not available");
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    } else {
                        Log.d("ServiceMenu", "No such document");
                        textView.setText("Email not available");
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    }
                } else {
                    Log.d("ServiceMenu", "get failed with ", task.getException());
                    textView.setText("Email not available");
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                }
            }
        });
    }

//    public String HandymenEmailFromDatabase(String FullName) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("HandyMen").document(FullName);
//
//        final String[] email = {null}; // Using an array to store the email and make it effectively final
//
//        docRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                email[0] = documentSnapshot.getString("Email");
//                Log.d("ServiceMenu", "DocumentSnapshot data: " + email[0]);
//            } else {
//                Log.d("ServiceMenu", "No such document");
//            }
//        }).addOnFailureListener(e -> {
//            Log.d("ServiceMenu", "get failed with ", e);
//        });
//
//        return email[0]; // Return null or handle the absence of data as needed
//    }

}





//
//        // Dynamically create and add TextViews
//        for (int i = 0; i < 5; i++) {
//            TextView handymanNameTextView = new TextView(this);
//            handymanNameTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            handymanNameTextView.setText("Name " + (i + 1));
//            handymanNameTextView.setTextSize(24); // Set text size to 24sp
//            handymanNameTextView.setPadding(0, 10, 0, 10); // Add top margin
//
//            // Add the TextView to the LinearLayout
//            containerLayout.addView(handymanNameTextView);
//
//            // Dynamically create and add DescriptionTextView
//            TextView descriptionTextView = new TextView(this);
//            descriptionTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    getResources().getDimensionPixelSize(R.dimen.description_text_height))); // Set height to 46dp
//            descriptionTextView.setText("Description of service " + (i + 1));
//            descriptionTextView.setPadding(0, 0, 0, 10); // Add bottom margin
//
//            // Add the DescriptionTextView to the LinearLayout
//            containerLayout.addView(descriptionTextView);
//        }
//    }

    // Function to check a Firestore document by ID
//    public void checkDocumentById(int documentId) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentRef = db.collection("HandyMen").document(String.valueOf(documentId));
//
//        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot document) {
//                    if (document.exists()) {
//                        // Document exists, retrieve the Full Name field
//                        String fullName = document.getString("Full Name");
//                        // Use fullName as needed
//                        // For example, you can store it in a variable or perform further actions
//                        // ...
//                    } else {
//                        // Document does not exist
//                    }
//                }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(Exception e) {
//                    // Error checking document
//                }});
//    }