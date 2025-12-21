package com.example.petcare.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.User;
import com.example.petcare.utils.PasswordUtil;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(v -> {
            if (AppDatabase.getInstance(this)
                    .userDao()
                    .checkEmail(email.getText().toString()) != null) {

                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.name = name.getText().toString();
            user.email = email.getText().toString();
            user.password = PasswordUtil.hashPassword(
                    password.getText().toString()
            );

            AppDatabase.getInstance(this).userDao().register(user);
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
