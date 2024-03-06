package com.example.handyproject;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

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
                                @Override
                                public void onClick(View v){TextViewValue(v);}
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
}







//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_service_menu);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Get references to the ScrollView and LinearLayout
//        ScrollView scrollView = findViewById(R.id.scrollView);
//        LinearLayout containerLayout = findViewById(R.id.containerLayout);
//
//        //
//        for (int Counter = 1; Counter <= 1000; Counter++) {
//            String CounterInString= String.valueOf(Counter);
//            // Your code to be executed for each iteration
//
//            db.collection("HandyMen").document(CounterInString).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//
//
//
//
//                        if (document.exists()) {
//                            Log.d("MainActivity", "DocumentSnapshot data: " + document.getData());
//                            String NameValue= document.getData().toString();
//                            TextView handymanNameTextView = new TextView(this);
//                            handymanNameTextView.setLayoutParams(new ViewGroup.LayoutParams(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT));
//                            handymanNameTextView.setText(NameValue);
//                            handymanNameTextView.setTextSize(24); // Set text size to 24sp
//                            handymanNameTextView.setPadding(0, 10, 0, 10); // Add top margin
//
//                            // Add the TextView to the LinearLayout
//                            containerLayout.addView(handymanNameTextView);
//
//                        } else {
//                            Log.d("MainActivity", "No such document");
//                        }
//                    } else {
//                        Log.d("MainActivity", "get failed with ", task.getException());
//                    }
//                }
//            });
//        }
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





//FOR DISPLAYING DYNAMICLY GENERATED UI TEXTVIEWS
//        // Get the reference to the LinearLayout
//        LinearLayout containerLayout = findViewById(R.id.containerLayout);
//
//        // Dynamically create and add TextViews
//        for (int i = 0; i < 5; i++) {
//            TextView textView = new TextView(this);
//            textView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            textView.setText("TextView " + (i + 1));
//
//            // Add the TextView to the LinearLayout
//            containerLayout.addView(textView);
//        }


                    //TO RETRIEVE FROM FIREBASE DATABASE
//        db.collection("HandyMen").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("ServiceMenu", document.getId() + " => " + document.getData());
//                        String AllHandymenEmails = document.getString("Full Name");
////                               //Initialize the TextView
//                        TextView HandymanNameTextView = findViewById(R.id.HandymanNameTextView);
//                        HandymanNameTextView.setText(AllHandymenEmails);
//
//
//                    }
//                } else {
//                    Log.w("ServiceMenu", "Error getting documents.", task.getException());
//                }
//            }
//        });
