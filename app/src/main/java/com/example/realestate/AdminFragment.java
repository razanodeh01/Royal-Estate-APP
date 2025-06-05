package com.example.realestate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private TextView registerTextView;
    private Button loginButton;
    private DatabaseHelper databaseHelper;
    private CheckBox rememberMeCheckbox;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        emailInput = view.findViewById(R.id.adminEmail);
        passwordInput = view.findViewById(R.id.adminPassword);
        loginButton = view.findViewById(R.id.adminLoginButton);
        rememberMeCheckbox = view.findViewById(R.id.rememberMeCheckBoxAdmin);
        registerTextView = view.findViewById(R.id.switchToRegisterAdmin);
        databaseHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("adminPrefs", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = databaseHelper.checkUser(email, password, "admin");
            if (isValid) {
                Toast.makeText(requireContext(), "Admin Login Successful", Toast.LENGTH_SHORT).show();

                if (rememberMeCheckbox.isChecked()) {
                    sharedPreferences.edit()
                            .putBoolean("remember", true)
                            .putString("email", email)
                            .putString("password", password)
                            .apply();
                } else {
                    sharedPreferences.edit().clear().apply();
                }

                Intent intent = new Intent(requireContext(), AdminHomeActivity.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegisterActivity.class);
            intent.putExtra("user_type", "admin");
            startActivity(intent);

        });

        // Load saved values if any
        boolean remember = sharedPreferences.getBoolean("remember", false);
        if (remember) {
            emailInput.setText(sharedPreferences.getString("email", ""));
            passwordInput.setText(sharedPreferences.getString("password", ""));
            rememberMeCheckbox.setChecked(true);
        }

        return view;
    }

}