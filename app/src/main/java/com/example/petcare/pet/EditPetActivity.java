package com.example.petcare.pet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.dao.PetDao;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;

import java.util.ArrayList;
import java.util.List;


public class EditPetActivity extends AppCompatActivity {

    // 🔥 Media URIs
    private Uri selectedImageUri;

    private List<Uri> selectedVideoUris = new ArrayList<>();
    private List<Uri> existingVideoUris = new ArrayList<>();


    // 🔥 Views
    private ImageView petPhoto;
    private VideoView petVideo;
    private Button pickImageBtn, pickVideoBtn, saveBtn;
    private EditText name, age, gender, likes;

    private Pet pet;

    private static final int PICK_IMAGE = 100;
    private static final int PICK_VIDEO = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        // 🔗 Bind views
        petPhoto = findViewById(R.id.petPhoto);
        petVideo = findViewById(R.id.petVideo);
        pickImageBtn = findViewById(R.id.pickImageBtn);
        pickVideoBtn = findViewById(R.id.pickVideoBtn);
        saveBtn = findViewById(R.id.saveBtn);

        name = findViewById(R.id.petName);
        age = findViewById(R.id.petAge);
        gender = findViewById(R.id.petGender);
        likes = findViewById(R.id.petLikes);

        // 🔹 Get petId sent from HomeActivity
        int petId = getIntent().getIntExtra("petId", -1);

        if (petId == -1) {
            Toast.makeText(this, "No pet selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Get DAO
        PetDao dao = AppDatabase.getInstance(this).petDao();

        // 🔹 Load THIS pet
        pet = dao.getPetById(petId);

        if (pet == null) {
            Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔁 Load existing data into UI…

        // 🔁 Load existing data
        name.setText(pet.name);
        age.setText(String.valueOf(pet.age));
        gender.setText(pet.gender);
        likes.setText(pet.likes);

        if (pet.photoUri != null) {
            selectedImageUri = Uri.parse(pet.photoUri);
            petPhoto.setImageURI(selectedImageUri);
        }

        if (pet.videoUris != null && !pet.videoUris.isEmpty()) {
            String[] videos = pet.videoUris.split(",");
            for (String v : videos) {
                existingVideoUris.add(Uri.parse(v));
            }

            // preview first existing video
            petVideo.setVideoURI(existingVideoUris.get(0));
        }



        pickImageBtn.setOnClickListener(v -> pickImage());
        pickVideoBtn.setOnClickListener(v -> pickVideo());
        saveBtn.setOnClickListener(v -> savePet());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // ⭐ IMPORTANT
        startActivityForResult(intent, PICK_VIDEO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == PICK_IMAGE) {

            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                selectedImageUri = uri;
                petPhoto.setImageURI(uri);
            }

        } else if (requestCode == PICK_VIDEO) {

            if (data.getClipData() != null) {
                // ✅ MULTIPLE VIDEOS
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri videoUri = data.getClipData().getItemAt(i).getUri();

                    getContentResolver().takePersistableUriPermission(
                            videoUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                    selectedVideoUris.add(videoUri);
                }

                petVideo.setVideoURI(selectedVideoUris.get(0));
                petVideo.start();

            } else {
                // ✅ SINGLE VIDEO
                Uri videoUri = data.getData();
                if (videoUri != null) {

                    getContentResolver().takePersistableUriPermission(
                            videoUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                    selectedVideoUris.add(videoUri);
                    petVideo.setVideoURI(videoUri);
                    petVideo.start();
                }
            }
        }
    }

    private void savePet() {
        pet.name = name.getText().toString();
        pet.age = Integer.parseInt(age.getText().toString());
        pet.gender = gender.getText().toString();
        pet.likes = likes.getText().toString();

        if (selectedImageUri != null) {
            pet.photoUri = selectedImageUri.toString();
        }

        if (!selectedVideoUris.isEmpty()) {
            StringBuilder builder = new StringBuilder();

// 1️⃣ Keep existing videos
            for (Uri uri : existingVideoUris) {
                builder.append(uri.toString()).append(",");
            }

// 2️⃣ Append new videos (avoid duplicates)
            for (Uri uri : selectedVideoUris) {
                if (!builder.toString().contains(uri.toString())) {
                    builder.append(uri.toString()).append(",");
                }
            }

// 3️⃣ Save back to DB
            pet.videoUris = builder.toString();

        }


        AppDatabase.getInstance(this)
                .petDao()
                .update(pet);

        Toast.makeText(this, "Pet updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
