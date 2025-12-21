package com.example.petcare.pet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import android.widget.Toast;

import com.example.petcare.utils.SessionManager;

public class PetSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_select);

        int userId = new SessionManager(this).getUserId();

        setPet(R.id.dogBtn, "Dog", userId);
        setPet(R.id.catBtn, "Cat", userId);
        setPet(R.id.fishBtn, "Fish", userId);
        setPet(R.id.birdBtn, "Bird", userId);
    }

    private void setPet(int buttonId, String type, int userId) {
        findViewById(buttonId).setOnClickListener(v -> {

            Pet pet = new Pet();
            pet.userId = userId;
            pet.type = type;

            long petId = AppDatabase.getInstance(this)
                    .petDao()
                    .insert(pet);

            // 🧪 DEBUG
            Toast.makeText(this, "Pet ID = " + petId, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, PetProfileActivity.class);
            intent.putExtra("PET_ID", petId);
            startActivity(intent);

            // ❗ TEMPORARILY COMMENT THIS
            // finish();
        });
    }

}
