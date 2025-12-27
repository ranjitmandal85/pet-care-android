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

    EditText petNameInput, petAgeInput, petGenderInput, petLikesInput;
    Button savePetBtn;

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
            petNameInput.setError("Required");
            return;
        }

        int age = ageText.isEmpty() ? 0 : Integer.parseInt(ageText);

        int userId = new SessionManager(this).getUserId();

        Pet pet = new Pet();
        pet.name = name;
        pet.age = age;
        pet.gender = gender;
        pet.likes = likes;
        pet.userId = userId;
        pet.photoUri = "";
        pet.videoUris = "";
        pet.type = "";

        new Thread(() -> {

            AppDatabase.getInstance(this)
                    .petDao()
                    .insert(pet);

            runOnUiThread(() -> {
                Toast.makeText(this, "Pet saved!", Toast.LENGTH_SHORT).show();
                finish();   // 👈 go back to HomeActivity
            });

        }).start();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
