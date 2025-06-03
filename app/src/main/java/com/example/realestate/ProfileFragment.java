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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class ProfileFragment extends Fragment {

    private EditText firstNameText, lastNameText, phoneText, passwordText, confirmPasswordText;
    private ImageView profilePicture;
    private Button uploadPictureButton;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private String profilePicturePath;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstNameText = view.findViewById(R.id.edit_first_name);
        lastNameText = view.findViewById(R.id.edit_last_name);
        phoneText = view.findViewById(R.id.edit_phone);
        passwordText = view.findViewById(R.id.edit_password);
        confirmPasswordText = view.findViewById(R.id.edit_confirm_password);
        profilePicture = view.findViewById(R.id.profile_picture);
        uploadPictureButton = view.findViewById(R.id.upload_picture_button);
        saveButton = view.findViewById(R.id.save_button);

        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        if (userEmail == null) {
            Toast.makeText(requireContext(), "User email not found. Please log in again.", Toast.LENGTH_LONG).show();
            requireActivity().finish();
            return view;
        }

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

        loadUserData();

        uploadPictureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        });

        saveButton.setOnClickListener(v -> saveProfileChanges());

        return view;
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor.moveToFirst()) {
            firstNameText.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            lastNameText.setText(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
            phoneText.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profile_picture"));
            if (profilePic != null) {
                File file = new File(profilePic);
                if (file.exists()) {
                    profilePicture.setImageBitmap(BitmapFactory.decodeFile(profilePic));
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

    private void saveProfileChanges() {
        String newFirst = firstNameText.getText().toString().trim();
        String newLast = lastNameText.getText().toString().trim();
        String newPhone = phoneText.getText().toString().trim();
        String newPass = passwordText.getText().toString().trim();
        String confirmPass = confirmPasswordText.getText().toString().trim();

        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (!cursor.moveToFirst()) return;

        String currentFirst = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
        String currentLast = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
        String currentPhone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
        cursor.close();

        if (!newFirst.equals(currentFirst)) {
            dbHelper.updateFirstName(userEmail, newFirst);
            Toast.makeText(requireContext(), "First name updated", Toast.LENGTH_SHORT).show();
        }
        if (!newLast.equals(currentLast)) {
            dbHelper.updateLastName(userEmail, newLast);
            Toast.makeText(requireContext(), "Last name updated", Toast.LENGTH_SHORT).show();
        }
        if (!newPhone.equals(currentPhone)) {
            dbHelper.updatePhone(userEmail, newPhone);
            Toast.makeText(requireContext(), "Phone number updated", Toast.LENGTH_SHORT).show();
        }

        if (!newPass.isEmpty()) {
            if (newPass.length() < 6 || !newPass.matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+")) {
                Toast.makeText(requireContext(), "Password must be 6+ characters with letter, number, and symbol", Toast.LENGTH_LONG).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updatePassword(userEmail, newPass);
            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
        }
    }
}
