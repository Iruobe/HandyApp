package com.example.handyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; // mAuth //shared instance of the FirebaseAuth object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //main
        mAuth= FirebaseAuth.getInstance();// Initialize FirebaseAuth instance to handle user authentication tasks
        Button signupButton = (Button) findViewById(R.id.LoginButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButtonClicked(view);
            }
        });


        //OnClickLister for handyman registration textview
        TextView handyManRegistration = findViewById(R.id.RegisterLinkTextView);
        handyManRegistration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent intent = new Intent(MainActivity.this, CustomerRegistration.class);
                Intent intent2 = new Intent(MainActivity.this, HandymanRegistration.class);
                startActivity(intent2);
            }
        });

        //OnClickLister for customer registration textview
        TextView customerRegistration = findViewById(R.id.RegisterLinkTextView2);
        customerRegistration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, CustomerRegistration.class);
                startActivity(intent);
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

    public void signin(String email, String password){//Checks user email and password and signs them in
        Task<AuthResult> mainActivity = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                    //user has been signed in, use an intent to move to the next activity
                    Intent intent = new Intent(MainActivity.this, ServiceMenu.class);
                    startActivity(intent);

                } else {
                    Log.w("MainActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void LoginButtonClicked(View view){
        EditText email = findViewById(R.id.NameTextText);
        EditText password = findViewById(R.id.EditTextTextPassword);

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        signin(sEmail, sPassword);
    }


}