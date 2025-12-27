package com.example.petcare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.pet.AddPetActivity;
import com.example.petcare.pet.HomeActivity;
import com.example.petcare.database.AppDatabase;
import com.example.petcare.models.Pet;
import com.example.petcare.models.User;
import com.example.petcare.utils.PasswordUtil;
import com.example.petcare.utils.SessionManager;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn, registerBtn;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> doLogin());
        registerBtn.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.petcare.auth.RegisterActivity.class))
        );
    }

    private void doLogin() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        User user = AppDatabase.getInstance(this).userDao().checkEmail(emailText);

        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!user.password.equals(PasswordUtil.hashPassword(passwordText))) {
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            return;
        }

        session.saveUserId(user.id);

        List<Pet> pets = AppDatabase.getInstance(this).petDao().getPetsByUserId(user.id);

        Intent nextActivity;
        if (pets == null || pets.isEmpty()) {
            nextActivity = new Intent(this, AddPetActivity.class);
        } else {
            nextActivity = new Intent(this, HomeActivity.class);
        }

        nextActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(nextActivity);
        finish();
    }
}
