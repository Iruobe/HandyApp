package com.example.handyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //main
        //setContentView(R.layout.activity_customer_registration); //customer registration page
        Intent intent = new Intent(this, CustomerRegistration.class);
        startActivity(intent);

    }
}