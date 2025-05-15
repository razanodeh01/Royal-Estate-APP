package com.example.realestate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView createAccountLink;
    CheckBox rememberMeCheckBox;
    SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "MyPrefs";
    public static final String KEY_EMAIL = "email";

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // UI
        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        createAccountLink = findViewById(R.id.switchToRegister); // add in XML

        // DB
        databaseHelper = new DatabaseHelper(this);

        // SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        if (!savedEmail.isEmpty()) {
            emailInput.setText(savedEmail);
            rememberMeCheckBox.setChecked(true);
        }

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = databaseHelper.checkUser(email, password);
            if (isValid) {
                // Remember email if checked
                if (rememberMeCheckBox.isChecked()) {
                    sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
                } else {
                    sharedPreferences.edit().remove(KEY_EMAIL).apply();
                }

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class)); // or HomeActivity
                finish();
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginRegisterActivity.this, RegisterActivity.class));
        });
    }
}
