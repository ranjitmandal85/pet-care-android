package com.example.petcare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.models.User;
import com.example.petcare.pet.HomeActivity;
import com.example.petcare.pet.PetSelectActivity;
import com.example.petcare.utils.PasswordUtil;
import com.example.petcare.utils.SessionManager;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> {

            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            User user = AppDatabase.getInstance(this)
                    .userDao()
                    .checkEmail(emailText);

            if (user == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedInput = PasswordUtil.hashPassword(passwordText);

            if (user.password.equals(hashedInput)) {

                // ✅ Save session
                new SessionManager(this).saveUserId(user.id);

                // ✅ CHECK PET HERE
                List<Pet> pet = AppDatabase.getInstance(this)
                        .petDao()
                        .getPetsByUserId(user.id);

                if (pet != null) {
                    // User already has pet → Home
                    startActivity(new Intent(this, HomeActivity.class));
                } else {
                    // No pet → Pet selection
                    startActivity(new Intent(this, PetSelectActivity.class));
                }

                finish();

            } else {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
