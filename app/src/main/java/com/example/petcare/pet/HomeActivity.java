package com.example.petcare.pet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.auth.LoginActivity;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ImageView petImage;
    private TextView petName, petAge, petGender, petLikes;
    private RecyclerView videoRecycler;

    private final List<Uri> videoUriList = new ArrayList<>();

    // 👇 this is needed for menu Edit option
    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        petImage = findViewById(R.id.petPhoto);
        petName = findViewById(R.id.petName);
        petAge = findViewById(R.id.petAge);
        petGender = findViewById(R.id.petGender);
        petLikes = findViewById(R.id.petLikes);
        videoRecycler = findViewById(R.id.videoRecycler);

        videoRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        loadPetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPetData();
    }

    // ========= MENU =========
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_add_pet) {

            startActivity(new Intent(this, AddPetActivity.class));
            return true;

        } else if (id == R.id.menu_edit_pet) {

            Intent editIntent = new Intent(this, EditPetActivity.class);
            editIntent.putExtra("petId", pet.id);
            startActivity(editIntent);
            return true;

        } else if (id == R.id.menu_logout) {

            getSharedPreferences("MyApp", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent login = new Intent(this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(login);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ========= LOAD PET =========
    private void loadPetData() {

        int userId = new SessionManager(this).getUserId();
        if (userId == -1) return;

        // ⚠️ Make sure your DAO returns ONE pet for a user
        pet = AppDatabase.getInstance(this)
                .petDao()
                .getPetByUserId(userId);

        if (pet == null) {
            startActivity(new Intent(this, PetSelectActivity.class));
            finish();
            return;
        }

        petName.setText(pet.name != null ? pet.name : "Pet");
        petAge.setText("Age: " + pet.age);
        petGender.setText("Gender: " + (pet.gender != null ? pet.gender : "N/A"));
        petLikes.setText("Likes: " + (pet.likes != null ? pet.likes : "N/A"));

        if (pet.photoUri != null && !pet.photoUri.isEmpty()) {
            petImage.setImageURI(Uri.parse(pet.photoUri));
        }

        videoUriList.clear();

        if (pet.videoUris != null && !pet.videoUris.isEmpty()) {
            String[] videos = pet.videoUris.split(",");
            for (String v : videos) {
                if (!v.trim().isEmpty()) videoUriList.add(Uri.parse(v));
            }
        }

        videoRecycler.setAdapter(new VideoAdapter(this, videoUriList));
    }
}
