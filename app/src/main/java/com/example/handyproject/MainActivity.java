package com.example.handyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button SendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //main


        //Lec Added code in the lab to display the registration page
//        Intent intent = new Intent(this, CustomerRegistration.class);
//        startActivity(intent);

        //OnClickLister for registration textview
        TextView textView = findViewById(R.id.RegisterLinkTextView);
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, CustomerRegistration.class);
                startActivity(intent);
            }
        });


    }
}