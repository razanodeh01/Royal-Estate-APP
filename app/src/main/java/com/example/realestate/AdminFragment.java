package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        emailInput = view.findViewById(R.id.adminEmail);
        passwordInput = view.findViewById(R.id.adminPassword);
        loginButton = view.findViewById(R.id.adminLoginButton);
        registerButton = view.findViewById(R.id.adminRegisterButton);
        databaseHelper = new DatabaseHelper(requireContext());

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
                Intent intent = new Intent(requireContext(), AdminHomeActivity.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdminRegisterActivity.class);
            startActivity(intent);
        });

        return view;
    }
}