package com.example.realestate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private TextView firstNameText, lastNameText, phoneText, passwordText;
    private Button editFirstNameButton, editLastNameButton, editPhoneButton, editPasswordButton;
    private ImageView profilePicture;
    private Button uploadPictureButton;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private String profilePicturePath;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        firstNameText = view.findViewById(R.id.first_name_text);
        lastNameText = view.findViewById(R.id.last_name_text);
        phoneText = view.findViewById(R.id.phone_text);
        passwordText = view.findViewById(R.id.password_text);
        editFirstNameButton = view.findViewById(R.id.edit_first_name_button);
        editLastNameButton = view.findViewById(R.id.edit_last_name_button);
        editPhoneButton = view.findViewById(R.id.edit_phone_button);
        editPasswordButton = view.findViewById(R.id.edit_password_button);
        profilePicture = view.findViewById(R.id.profile_picture);
        uploadPictureButton = view.findViewById(R.id.upload_picture_button);
        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        if (userEmail == null) {
            Toast.makeText(requireContext(), "User email not found. Please log in again.", Toast.LENGTH_LONG).show();
            requireActivity().finish();
            return view;
        }

        // Initialize launchers
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    profilePicture.setImageBitmap(bitmap);
                    profilePicturePath = saveImageToInternalStorage(bitmap);
                    if (dbHelper.updateProfilePicture(userEmail, profilePicturePath)) {
                        Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        // Load user data
        loadUserData();

        // Set up edit buttons
        editFirstNameButton.setOnClickListener(v -> showEditDialog("First Name", firstNameText.getText().toString(), this::updateFirstName));
        editLastNameButton.setOnClickListener(v -> showEditDialog("Last Name", lastNameText.getText().toString(), this::updateLastName));
        editPhoneButton.setOnClickListener(v -> showEditDialog("Phone Number", phoneText.getText().toString(), this::updatePhone));
        editPasswordButton.setOnClickListener(v -> showPasswordEditDialog());

        // Upload picture
        uploadPictureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        });

        return view;
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profile_picture"));

            firstNameText.setText(firstName);
            lastNameText.setText(lastName);
            phoneText.setText(phone);
            if (profilePic != null) {
                File file = new File(profilePic);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(profilePic);
                    profilePicture.setImageBitmap(bitmap);
                    profilePicturePath = profilePic;
                }
            }
        }
        cursor.close();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File file = new File(requireContext().getFilesDir(), "profile_" + userEmail.replace("@", "_") + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void showEditDialog(String field, String currentValue, UpdateCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit " + field);

        EditText input = new EditText(requireContext());
        input.setText(currentValue);
        input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (field.equals("Phone Number")) {
            input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        } else {
            input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (validateInput(field, newValue)) {
                callback.update(newValue);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPasswordEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Password");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        EditText currentPasswordInput = new EditText(requireContext());
        currentPasswordInput.setHint("Current Password");
        currentPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(currentPasswordInput);

        EditText newPasswordInput = new EditText(requireContext());
        newPasswordInput.setHint("New Password");
        newPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPasswordInput);

        EditText confirmPasswordInput = new EditText(requireContext());
        confirmPasswordInput.setHint("Confirm New Password");
        confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmPasswordInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String currentPassword = currentPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (!dbHelper.verifyPassword(userEmail, currentPassword)) {
                Toast.makeText(requireContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6 || !newPassword.matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+")) {
                Toast.makeText(requireContext(), "New password must be 6+ characters & include letter, number, symbol", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updatePassword(userEmail, newPassword)) {
                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean validateInput(String field, String value) {
        if (field.equals("First Name") || field.equals("Last Name")) {
            if (value.length() < 3) {
                Toast.makeText(requireContext(), field + " must be at least 3 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (field.equals("Phone Number")) {
            String phoneDigits = value.replaceAll("[^\\d]", "");
            if (phoneDigits.length() < 10) {
                Toast.makeText(requireContext(), "Phone must be at least 10 digits", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void updateFirstName(String value) {
        if (dbHelper.updateFirstName(userEmail, value)) {
            firstNameText.setText(value);
            Toast.makeText(requireContext(), "First name updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update first name", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLastName(String value) {
        if (dbHelper.updateLastName(userEmail, value)) {
            lastNameText.setText(value);
            Toast.makeText(requireContext(), "Last name updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update last name", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePhone(String value) {
        if (dbHelper.updatePhone(userEmail, value)) {
            phoneText.setText(value);
            Toast.makeText(requireContext(), "Phone number updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private interface UpdateCallback {
        void update(String value);
    }
}