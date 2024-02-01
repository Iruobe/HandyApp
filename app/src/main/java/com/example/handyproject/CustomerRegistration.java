package com.example.handyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerRegistration extends AppCompatActivity {
    private FirebaseAuth mAuth; // mAuth //shared instance of the FirebaseAuth object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);
        mAuth= FirebaseAuth.getInstance();// Initialize FirebaseAuth instance to handle user authentication tasks


        //ID of components link to disappearing UI Elements
        Switch aSwitch = findViewById(R.id.RoleSwitch);
        Spinner ExperienceSpinner = findViewById(R.id.ExperienceSpinner); //Spinner for experience level
        ImageView ImageView= findViewById(R.id.HandymanImageView);// Image view for handyman picture
        TextView ServiceTextView = findViewById(R.id.ServiceEditTextText);
        TextView PricePerHourEditText = findViewById(R.id.PricePerHourEditTextNumberDecimal);
        //On change listener for disappearing UI elements with switch
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Hide elements
                ServiceTextView.setVisibility(View.GONE);
                PricePerHourEditText.setVisibility(View.GONE);
                ExperienceSpinner.setVisibility(View.GONE);
                ImageView.setVisibility(View.VISIBLE);
            } else {
                // Show elements
                ServiceTextView.setVisibility(View.VISIBLE);
                PricePerHourEditText.setVisibility(View.VISIBLE);
                ExperienceSpinner.setVisibility(View.VISIBLE);
                ImageView.setVisibility(View.VISIBLE);
            }
        });

        //Spinner for experience level
        // options for Experience Spinner
        String[] ExperienceOptions = new String[]{"Beginner", "Intermediate", "Experienced","Specialist", "Consultant"};
        // Created an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ExperienceOptions);
        //Layout when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ExperienceSpinner.setAdapter(adapter);



        //Signup Button
        Button signupButton = (Button) findViewById(R.id.button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupButtonClicked(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //User is signed in use an intent to move to another activity
        }
    }

    public void signup(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("CustomerRegistration",
                                            "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(CustomerRegistration.this, "Authentication success. Use an intent to move to a new activity", Toast.LENGTH_SHORT).show(); ////////////ASK IN LAB
                                    //user has been signed in, use an intent to
                                    //move to the next activity

                                } else {// If sign in fails, display a message to the user.
                                    Log.w("CustomerRegistration","createUserWithEmail:failure", task.getException());
                                    Toast.makeText(CustomerRegistration.this, "Authentication failed.",Toast.LENGTH_SHORT).show(); ////////////ASK IN LAB
                                }
                            }
                    });
    }

    //Method called when signup button clicked.
    public void signupButtonClicked(View view){
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText password = findViewById(R.id.editTextTextPassword);

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        signup(sEmail, sPassword);
    }
}




