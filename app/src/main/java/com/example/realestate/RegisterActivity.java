package com.example.realestate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, firstNameInput, lastNameInput, passwordInput, confirmPasswordInput, phoneInput;
    Spinner genderSpinner, countrySpinner, citySpinner;
    Button registerButton;

    // Gender Options
    String[] genders = {"Select Gender", "Male", "Female", "Other"};

    // Country → Cities Mapping
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

                boolean insertResult = databaseHelper.insertUser(email, firstName, lastName, password, gender, country, city, phone);

                if (insertResult) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Registration Failed: Email may already exist.", Toast.LENGTH_LONG).show();
                }
            }
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
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void setupCountryCityData() {
        countryCityMap.put("Select Country", new String[]{"Select City"});
        countryCityMap.put("Palestine", new String[]{"Ramallah", "Nablus", "Hebron"});
        countryCityMap.put("Jordan", new String[]{"Amman", "Irbid", "Zarqa"});
        countryCityMap.put("Egypt", new String[]{"Cairo", "Alexandria", "Giza"});
    }

    private void setupCountrySpinner() {
        String[] countries = countryCityMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries);
        countrySpinner.setAdapter(countryAdapter);

        // Handle country selection change
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                updateCitySpinner(selectedCountry);
                updatePhoneCode(selectedCountry);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void updateCitySpinner(String country) {
        String[] cities = countryCityMap.get(country);
        if (cities == null) cities = new String[]{"Select City"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
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

        // Email validation
        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailInput.setError("Invalid email format");
            return false;
        }

        // First and last name validation
        if (firstName.length() < 3) {
            firstNameInput.setError("First name must be at least 3 characters");
            return false;
        }

        if (lastName.length() < 3) {
            lastNameInput.setError("Last name must be at least 3 characters");
            return false;
        }

        // Password validation
        if (password.length() < 6 || !password.matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+")) {
            passwordInput.setError("Password must be 6+ characters & include letter, number, symbol");
            return false;
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }

        // Gender selection
        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Country and city
        if (country.equals("Select Country") || city.equals("Select City")) {
            Toast.makeText(this, "Please select a valid country and city", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Phone number length (after country code)
        String phoneDigits = phone.replaceAll("[^\\d]", "");
        if (phoneDigits.length() < 10) {
            phoneInput.setError("Phone must be at least 10 digits");
            return false;
        }

        return true;
    }
    private void saveUserData(String email, String firstName, String lastName, String password, String gender, String country, String city, String phone) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("email", email);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("password", password); // In production, hash the password!
        editor.putString("gender", gender);
        editor.putString("country", country);
        editor.putString("city", city);
        editor.putString("phone", phone);

        editor.apply();
    }


}
