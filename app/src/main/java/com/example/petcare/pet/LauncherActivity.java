package com.example.petcare.pet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.auth.LoginActivity;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.utils.SessionManager;

import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        int userId = session.getUserId();
        List<Pet> pets = AppDatabase.getInstance(this).petDao().getPetsByUserId(userId);

        if (pets == null || pets.isEmpty()) {
            // First-time user → AddPetActivity
            startActivity(new Intent(this, AddPetActivity.class));
        } else {
            // Existing user → HomeActivity
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish(); // Finish LauncherActivity
    }
}
