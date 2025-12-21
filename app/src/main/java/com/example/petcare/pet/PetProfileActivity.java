package com.example.petcare.pet;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petcare.R;

import android.widget.Button;
import android.widget.TextView;
import com.example.petcare.models.Pet;
import com.example.petcare.database.AppDatabase;
import android.widget.Toast;






public class PetProfileActivity extends AppCompatActivity {

    TextView petType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        Button editBtn = findViewById(R.id.editBtn);


        TextView petType = findViewById(R.id.petType);

        long petId = getIntent().getLongExtra("PET_ID", -1);

        Toast.makeText(this, "Received PET_ID = " + petId, Toast.LENGTH_LONG).show();
        if (petId == -1) {
            Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Pet pet = AppDatabase.getInstance(this)
                .petDao()
                .getPetById(petId);

        if (pet == null) {
            Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        petType.setText(pet.type);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPetActivity.class);
            intent.putExtra("PET_ID", petId);
            startActivity(intent);
        });

    }
}
