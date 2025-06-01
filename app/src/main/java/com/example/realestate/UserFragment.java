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

public class UserFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView createAccountLink;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    public static final String PREFS_NAME = "MyPrefs";
    public static final String KEY_EMAIL = "email";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        emailInput = view.findViewById(R.id.loginEmail);
        passwordInput = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);
        rememberMeCheckBox = view.findViewById(R.id.rememberMeCheckBox);
        createAccountLink = view.findViewById(R.id.switchToRegister);
        databaseHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        if (!savedEmail.isEmpty()) {
            emailInput.setText(savedEmail);
            rememberMeCheckBox.setChecked(true);
        }

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = databaseHelper.checkUser(email, password, "user");
            if (isValid) {
                if (rememberMeCheckBox.isChecked()) {
                    sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
                } else {
                    sharedPreferences.edit().remove(KEY_EMAIL).apply();
                }

                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), HomeActivity.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountLink.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RegisterActivity.class));
        });

        return view;
    }
}