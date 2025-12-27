package com.example.petcare.pet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.utils.SessionManager;

public class AddPetActivity extends AppCompatActivity {

    private EditText petNameInput, petAgeInput, petGenderInput, petLikesInput;
    private Button savePetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        petNameInput = findViewById(R.id.petName);
        petAgeInput = findViewById(R.id.petAge);
        petGenderInput = findViewById(R.id.petGender);
        petLikesInput = findViewById(R.id.petLikes);
        savePetBtn = findViewById(R.id.savePetBtn);

        savePetBtn.setOnClickListener(v -> savePet());
    }

    private void savePet() {
        String name = petNameInput.getText().toString().trim();
        String ageText = petAgeInput.getText().toString().trim();
        String gender = petGenderInput.getText().toString().trim();
        String likes = petLikesInput.getText().toString().trim();

        if (name.isEmpty()) {
            petNameInput.setError("Name is required");
            return;
        }

        int age = 0;
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                petAgeInput.setError("Invalid age");
                return;
            }
        }

        int userId = new SessionManager(this).getUserId();
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Pet pet = new Pet();
        pet.name = name;
        pet.age = age;
        pet.gender = gender;
        pet.likes = likes;
        pet.userId = userId;
        pet.photoUri = "";  // Can be updated later
        pet.videoUris = ""; // Can be updated later
        pet.type = "";      // Optional

        // Save pet in background thread
        new Thread(() -> {
            AppDatabase.getInstance(this).petDao().insert(pet);

            runOnUiThread(() -> {
                Toast.makeText(this, "Pet saved!", Toast.LENGTH_SHORT).show();
                // Return to HomeActivity, refresh handled in onResume()
                finish();
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optionally, you can handle first-time user back press differently
        // For example, prevent going back if no pets exist yet
    }
}
