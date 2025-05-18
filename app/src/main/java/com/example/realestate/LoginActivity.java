package com.example.realestate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private CheckBox rememberMeCheckbox;
    private Button loginButton;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Initialize UI elements
        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);


        // Initialize helper and preferences
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Load saved email if Remember Me was checked
        String savedEmail = sharedPreferences.getString("remembered_email", "");
        if (!savedEmail.isEmpty()) {
            emailInput.setText(savedEmail);
            rememberMeCheckbox.setChecked(true);
        }

        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            // Basic validation
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password is required");
                return;
            }

            // Check user in database
            boolean userExists = databaseHelper.checkUser(email, password);

            if (userExists) {
                // Save or remove remembered email
                if (rememberMeCheckbox.isChecked()) {
                    sharedPreferences.edit().putString("remembered_email", email).apply();
                } else {
                    sharedPreferences.edit().remove("remembered_email").apply();
                }

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
