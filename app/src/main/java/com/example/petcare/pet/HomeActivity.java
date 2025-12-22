package com.example.petcare.pet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // UI
    private Button editPetBtn;
    private ImageView petImage;
    private TextView petName, petAge, petGender, petLikes;
    private RecyclerView videoRecycler;

    // Data
    private final List<Uri> videoUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Bind views
        editPetBtn = findViewById(R.id.editPetBtn);
        petImage = findViewById(R.id.petPhoto);
        petName = findViewById(R.id.petName);
        petAge = findViewById(R.id.petAge);
        petGender = findViewById(R.id.petGender);
        petLikes = findViewById(R.id.petLikes);
        videoRecycler = findViewById(R.id.videoRecycler);

        videoRecycler.setLayoutManager(
                new GridLayoutManager(this, 2)
        );


        editPetBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EditPetActivity.class))
        );

        loadPetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPetData();
    }

    // 📦 LOAD PET DATA
    private void loadPetData() {

        int userId = new SessionManager(this).getUserId();
        if (userId == -1) return;

        Pet pet = AppDatabase.getInstance(this)
                .petDao()
                .getPetByUserId(userId);

        if (pet == null) {
            startActivity(new Intent(this, PetSelectActivity.class));
            finish();
            return;
        }

        // TEXT
        petName.setText(pet.name != null ? pet.name : "Pet");
        petAge.setText("Age: " + pet.age);
        petGender.setText("Gender: " + (pet.gender != null ? pet.gender : "N/A"));
        petLikes.setText("Likes: " + (pet.likes != null ? pet.likes : "N/A"));

        // IMAGE
        if (pet.photoUri != null && !pet.photoUri.isEmpty()) {
            petImage.setImageURI(Uri.parse(pet.photoUri));
        }

        // VIDEOS
        videoUriList.clear();

        if (pet.videoUris != null && !pet.videoUris.isEmpty()) {

            String[] videos = pet.videoUris.split(",");

            for (String v : videos) {
                if (!v.trim().isEmpty()) {
                    videoUriList.add(Uri.parse(v));
                }
            }
        }

        videoRecycler.setAdapter(
                new VideoAdapter(this, videoUriList)
        );
    }
}
