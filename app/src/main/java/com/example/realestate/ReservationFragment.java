package com.example.realestate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        TextView propertyIdView = view.findViewById(R.id.property_id);
        Button confirmButton = view.findViewById(R.id.confirm_reservation);

        Bundle args = getArguments();
        if (args != null) {
            int propertyId = args.getInt("property_id");
            String userEmail = args.getString("user_email");
            propertyIdView.setText("Property ID: " + propertyId);

            confirmButton.setOnClickListener(v -> {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                boolean added = dbHelper.addReservation(userEmail, propertyId, date);
                Toast.makeText(requireContext(), added ? "Reservation Confirmed" : "Reservation Failed", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        return view;
    }
}