package com.example.petcare.pet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;

public class EditPetActivity extends AppCompatActivity {

    EditText name, age, gender, likes;
    Button saveBtn;
    long petId;
    Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        petId = getIntent().getLongExtra("PET_ID", -1);

        if (petId == -1) {
            finish();
            return;
        }

        pet = AppDatabase.getInstance(this)
                .petDao()
                .getPetById(petId);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        likes = findViewById(R.id.likes);
        saveBtn = findViewById(R.id.saveBtn);

        // Pre-fill data
        name.setText(pet.name);
        age.setText(pet.age == 0 ? "" : String.valueOf(pet.age));
        gender.setText(pet.gender);
        likes.setText(pet.likes);

        saveBtn.setOnClickListener(v -> {
            pet.name = name.getText().toString();
            pet.age = age.getText().toString().isEmpty()
                    ? 0
                    : Integer.parseInt(age.getText().toString());
            pet.gender = gender.getText().toString();
            pet.likes = likes.getText().toString();

            AppDatabase.getInstance(this).petDao().update(pet);
            Toast.makeText(this, "Pet updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
