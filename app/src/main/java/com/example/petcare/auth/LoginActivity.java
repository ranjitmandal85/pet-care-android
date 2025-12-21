package com.example.petcare.auth;
import com.example.petcare.utils.PasswordUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.User;
import com.example.petcare.pet.PetSelectActivity;
import com.example.petcare.utils.SessionManager;

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

            String hashedInput = com.example.petcare.utils.PasswordUtil.hashPassword(passwordText);

            if (user.password.equals(hashedInput)) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                new SessionManager(this).saveUserId(user.id);
                startActivity(new Intent(this, PetSelectActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
        startActivity(new Intent(LoginActivity.this, PetSelectActivity.class));
    }
}
