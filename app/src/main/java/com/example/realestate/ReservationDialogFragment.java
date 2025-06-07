/**
 * Description:
 * This class implements a bottom sheet dialog fragment that allows users to confirm a reservation
 * for a specific property in a smooth and intuitive popup interface.
 */

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ReservationDialogFragment extends BottomSheetDialogFragment {

    private DatabaseHelper dbHelper;

    public static ReservationDialogFragment newInstance(int propertyId, String userEmail) {
        ReservationDialogFragment fragment = new ReservationDialogFragment();
        Bundle args = new Bundle();
        args.putInt("property_id", propertyId);
        args.putString("user_email", userEmail);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        TextView propertyIdView = view.findViewById(R.id.property_id);
        Button confirmButton = view.findViewById(R.id.confirm_reservation);

        int propertyId = getArguments().getInt("property_id");
        String userEmail = getArguments().getString("user_email");

        propertyIdView.setText("Property ID: " + propertyId);

        confirmButton.setOnClickListener(v -> {
            if (dbHelper.isPropertyReserved(userEmail, propertyId)) {
                Toast.makeText(getContext(), "Already reserved", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            boolean added = dbHelper.addReservation(userEmail, propertyId, date);
            Toast.makeText(getContext(),
                    added ? "Reservation Confirmed" : "Reservation Failed",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return view;
    }
}
