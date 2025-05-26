package com.example.realestate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {

    private Button callUsButton, locateUsButton, emailUsButton;
    private ActivityResultLauncher<String> callPermissionLauncher;
    private static final String AGENCY_PHONE = "tel:+970599000000";
    private static final String AGENCY_EMAIL = "RealEstateHub@agency.com";
    private static final String AGENCY_LOCATION = "geo:31.9048,35.2034?q=RealEstateHub"; // Ramallah coordinates

    public ContactFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // Initialize buttons
        callUsButton = view.findViewById(R.id.call_us_button);
        locateUsButton = view.findViewById(R.id.locate_us_button);
        emailUsButton = view.findViewById(R.id.email_us_button);

        // Initialize permission launcher
        callPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                startCall();
            } else {
                Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        // Set button listeners
        callUsButton.setOnClickListener(v -> handleCallAction());
        locateUsButton.setOnClickListener(v -> handleLocateAction());
        emailUsButton.setOnClickListener(v -> handleEmailAction());

        return view;
    }

    private void handleCallAction() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        } else {
            startCall();
        }
    }

    private void startCall() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(AGENCY_PHONE));
        try {
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open phone app", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLocateAction() {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AGENCY_LOCATION));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open Google Maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEmailAction() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + AGENCY_EMAIL));
        try {
            startActivity(emailIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open email app", Toast.LENGTH_SHORT).show();
        }
    }
}