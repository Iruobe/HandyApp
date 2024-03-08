package com.example.handyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;




public class HandymanRegistration extends AppCompatActivity {
    private FirebaseAuth mAuth;//shared instance of the FirebaseAuth object
    FirebaseFirestore db = FirebaseFirestore.getInstance();// Intitialize firebase firestore database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handyman_registration);
        mAuth = FirebaseAuth.getInstance();// Initialize FirebaseAuth instance to handle user authentication tasks

        //ID of components link to disappearing UI Elements
        Spinner ExperienceSpinner = findViewById(R.id.ExperienceSpinner); //Spinner for experience level

        // options for Experience Spinner
        String[] ExperienceOptions = new String[]{"Beginner", "Intermediate", "Experienced", "Specialist", "Consultant"};
        // Created an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ExperienceOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//Layout when list of choices appears
        ExperienceSpinner.setAdapter(adapter);
        //Signup Button
        Button signupButton = (Button) findViewById(R.id.button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {signupButtonClicked(view);}
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //User is signed in use an intent to move to another activity
        }
    }

    //Takes user details and adds to database as well as creates the user
    public void signup(String email, String password, String fullName, String pricePerHour, String location, String service) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("CustomerRegistration", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(HandymanRegistration.this, "Authentication success. Use an intent to move to a new activity", Toast.LENGTH_SHORT).show(); ////////////ASK IN LAB


                            //Handymen details to be added to database
                            //Map Storing the retrieved data from UI and handyman input
                            Map<String, Object> HandyMen = new HashMap<>();
                            HandyMen.put("Email", email);
                            HandyMen.put("Full Name", fullName);
                            HandyMen.put("Location", location);
                            HandyMen.put("Service Provided", service);
                            HandyMen.put("Rate Charged", pricePerHour);

                            // Add a new document with full name as ID
                            db.collection("HandyMen").document(fullName)
                                    .set(HandyMen).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("HandymanRegistration","DocumentSnapshot successfully written!");
                                        }
                                    })


                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                            Log.w("HandymanRegistration", "Error adding document", e);
                                        }
                                    });


                            //user has been signed in, use an intent to move to the next activity
                            Intent intent = new Intent(HandymanRegistration.this, ServiceMenu.class);
                            startActivity(intent);

                        } else {// If sign in fails, display a message to the user.
                            Log.w("CustomerRegistration", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(HandymanRegistration.this, "Authentication failed.", Toast.LENGTH_SHORT).show(); ////////////ASK IN LAB
                        }
                    }
                });
    }

    //Method called when signup button clicked.
    public void signupButtonClicked(View view) {
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);
        //handyman details
        //ImageView ImageView= findViewById(R.id.HandymanImageView);// Image view for handyman picture
        EditText FullName = findViewById(R.id.FullNameEditTextText);
        EditText Location = findViewById(R.id.LocationEditTextText);
        EditText ServiceTextView = findViewById(R.id.ServiceEditTextText);
        EditText PricePerHourEditText = findViewById(R.id.PricePerHourEditTextNumberDecimal);
        //Casting user input to string.Format to be added to database
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        String ServiceInString = ServiceTextView.getText().toString();
        String PricePerHourInString = PricePerHourEditText.getText().toString();
        String FullNameInString = FullName.getText().toString();
        String LocationInString = Location.getText().toString();

        signup(sEmail, sPassword, FullNameInString, PricePerHourInString, LocationInString, ServiceInString);
    }

}