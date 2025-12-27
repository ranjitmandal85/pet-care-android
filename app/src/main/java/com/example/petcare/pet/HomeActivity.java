package com.example.petcare.pet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    private Spinner petSpinner;
    private List<Pet> pets = new ArrayList<>();
    private Pet selectedPet;

    private ImageView petImage;
    private TextView petName, petAge, petGender, petLikes;
    private RecyclerView videoRecycler;

    private final List<Uri> videoUriList = new ArrayList<>();
    private int spinnerSelectedPosition = 0;

    // Launcher to start AddPetActivity
    private final ActivityResultLauncher<Intent> addPetLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> loadPetList() // refresh pet list after adding
    );

    // Launcher to start EditPetActivity
    private final ActivityResultLauncher<Intent> editPetLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> loadPetList() // refresh pet list after editing
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        petSpinner = findViewById(R.id.petSpinner);
        petImage = findViewById(R.id.petPhoto);
        petName = findViewById(R.id.petName);
        petAge = findViewById(R.id.petAge);
        petGender = findViewById(R.id.petGender);
        petLikes = findViewById(R.id.petLikes);
        videoRecycler = findViewById(R.id.videoRecycler);
        videoRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        loadPetList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore spinner selection
        if (pets.size() > spinnerSelectedPosition) {
            petSpinner.setSelection(spinnerSelectedPosition);
        }
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
            addPetLauncher.launch(new Intent(this, AddPetActivity.class));
            return true;

        } else if (id == R.id.menu_edit_pet) {
            if (selectedPet != null) {
                Intent editIntent = new Intent(this, EditPetActivity.class);
                editIntent.putExtra("petId", selectedPet.id);
                editPetLauncher.launch(editIntent);
            } else {
                Toast.makeText(this, "No pet selected", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.menu_delete_pet) {
            if (selectedPet != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Pet")
                        .setMessage("Are you sure you want to delete " + selectedPet.name + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                AppDatabase.getInstance(this).petDao().delete(selectedPet);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, selectedPet.name + " deleted", Toast.LENGTH_SHORT).show();
                                    loadPetList();
                                    if (!pets.isEmpty()) {
                                        selectedPet = pets.get(0);
                                        petSpinner.setSelection(0);
                                        showPet(selectedPet);
                                    } else {
                                        addPetLauncher.launch(new Intent(this, AddPetActivity.class));
                                    }
                                });
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                Toast.makeText(this, "No pet selected", Toast.LENGTH_SHORT).show();
            }
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

    // ========= LOAD PET LIST =========
    private void loadPetList() {

        int userId = new SessionManager(this).getUserId();
        if (userId == -1) return;

        new Thread(() -> {
            pets = AppDatabase.getInstance(this).petDao().getPetsByUserId(userId);

            runOnUiThread(() -> {
                if (pets == null || pets.isEmpty()) {
                    // First-time user -> launch AddPetActivity
                    addPetLauncher.launch(new Intent(this, AddPetActivity.class));
                    return;
                }

                List<String> petNames = new ArrayList<>();
                for (Pet p : pets) {
                    petNames.add(p.name != null ? p.name : "Unnamed Pet");
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, petNames);
                petSpinner.setAdapter(adapter);

                petSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        selectedPet = pets.get(position);
                        spinnerSelectedPosition = position;
                        showPet(selectedPet);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });

                if (selectedPet == null && !pets.isEmpty()) {
                    selectedPet = pets.get(0);
                    showPet(selectedPet);
                }
            });
        }).start();
    }

    // ========= DISPLAY PET =========
    private void showPet(Pet pet) {
        if (pet == null) return;

        petName.setText(pet.name != null ? pet.name : "Pet");
        petAge.setText("Age: " + pet.age);
        petGender.setText("Gender: " + (pet.gender != null ? pet.gender : "N/A"));
        petLikes.setText("Likes: " + (pet.likes != null ? pet.likes : "N/A"));

        if (pet.photoUri != null && !pet.photoUri.isEmpty()) {
            petImage.setImageURI(Uri.parse(pet.photoUri));
        } else {
            petImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        videoUriList.clear();
        if (pet.videoUris != null && !pet.videoUris.isEmpty()) {
            String[] videos = pet.videoUris.split(",");
            for (String v : videos) {
                if (v != null && !v.trim().isEmpty()) {
                    videoUriList.add(Uri.parse(v));
                }
            }
        }

        VideoAdapter adapter = new VideoAdapter(this, videoUriList);
        videoRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
