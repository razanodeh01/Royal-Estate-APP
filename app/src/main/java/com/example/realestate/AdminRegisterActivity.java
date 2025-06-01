package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class AdminRegisterActivity extends AppCompatActivity {

    EditText emailInput, firstNameInput, lastNameInput, passwordInput, confirmPasswordInput, phoneInput;
    Spinner genderSpinner, countrySpinner, citySpinner;
    Button registerButton;
    TextView switchToLogin;
    String[] genders = {"Select Gender", "Male", "Female", "Other"};
    HashMap<String, String[]> countryCityMap = new HashMap<>();
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);
        initViews();
        setupCountryCityData();
        setupGenderSpinner();
        setupCountrySpinner();

        registerButton.setOnClickListener(view -> {
            if (validateInputs()) {
                String email = emailInput.getText().toString().trim();
                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String country = countrySpinner.getSelectedItem().toString();
                String city = citySpinner.getSelectedItem().toString();
                String phone = phoneInput.getText().toString().trim();

                boolean insertResult = databaseHelper.insertUser(email, firstName, lastName, password, gender, country, city, phone, "admin");
                if (insertResult) {
                    Toast.makeText(this, "Admin Registration Successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdminRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Registration Failed: Email may already exist.", Toast.LENGTH_LONG).show();
                }
            }
        });

        switchToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        phoneInput = findViewById(R.id.phoneInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);
        citySpinner = findViewById(R.id.citySpinner);
        registerButton = findViewById(R.id.registerButton);
        switchToLogin = findViewById(R.id.switchToLogIn);
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, genders);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void setupCountryCityData() {
        countryCityMap.put("Select Country", new String[]{"Select City"});
        countryCityMap.put("Palestine", new String[]{"Ramallah", "Nablus", "Hebron"});
        countryCityMap.put("Jordan", new String[]{"Amman", "Irbid", "Zarqa"});
        countryCityMap.put("Egypt", new String[]{"Cairo", "Alexandria", "Giza"});
    }

    private void setupCountrySpinner() {
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, countryCityMap.keySet().toArray(new String[0]));
        countryAdapter.setDropDownViewResource(R.layout.spinner_item);
        countrySpinner.setAdapter(countryAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                updateCitySpinner(selectedCountry);
                updatePhoneCode(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateCitySpinner(String country) {
        String[] cities = countryCityMap.get(country);
        if (cities == null) cities = new String[]{"Select City"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cities);
        cityAdapter.setDropDownViewResource(R.layout.spinner_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void updatePhoneCode(String country) {
        String prefix = "";
        switch (country) {
            case "Palestine":
                prefix = "+970";
                break;
            case "Jordan":
                prefix = "+962";
                break;
            case "Egypt":
                prefix = "+20";
                break;
        }
        phoneInput.setText(prefix);
    }

    private boolean validateInputs() {
        String email = emailInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();
        String phone = phoneInput.getText().toString().trim();

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailInput.setError("Invalid email format");
            return false;
        }

        if (firstName.length() < 3) {
            firstNameInput.setError("First name must be at least 3 characters");
            return false;
        }

        if (lastName.length() < 3) {
            lastNameInput.setError("Last name must be at least 3 characters");
            return false;
        }

        if (password.length() < 6 || !password.matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+")) {
            passwordInput.setError("Password must be 6+ characters & include letter, number, symbol");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }

        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (country.equals("Select Country") || city.equals("Select City")) {
            Toast.makeText(this, "Please select a valid country and city", Toast.LENGTH_SHORT).show();
            return false;
        }

        String phoneDigits = phone.replaceAll("[^\\d]", "");
        if (phoneDigits.length() < 10) {
            phoneInput.setError("Phone must be at least 10 digits");
            return false;
        }

        return true;
    }
}