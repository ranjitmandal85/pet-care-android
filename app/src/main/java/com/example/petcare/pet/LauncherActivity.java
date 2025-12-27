package com.example.petcare.pet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.auth.LoginActivity;
import com.example.petcare.utils.SessionManager;

public class LauncherActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            // User already logged in → go to Home
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // User not logged in → go to Login
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // Close LauncherActivity
    }
}
