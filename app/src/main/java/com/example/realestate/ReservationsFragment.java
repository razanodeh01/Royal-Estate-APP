/**
 * Description:
 * This class represents the "Your Reservations" screen in the user interface,
 * allowing users to view a list of properties they have successfully reserved.
 */

package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private ReservationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);

        recyclerView = view.findViewById(R.id.reservations_recycler);
        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        loadReservations();

        return view;
    }

    private void loadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = dbHelper.getReservationsByUser(userEmail);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String area = cursor.getString(cursor.getColumnIndexOrThrow("area"));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms"));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String reservationDate = cursor.getString(cursor.getColumnIndexOrThrow("reservation_date"));

                Property property = new Property(id, title, type, price, location, area, bedrooms, bathrooms, imageUrl, description);
                reservations.add(new Reservation(property, reservationDate));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (adapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ReservationAdapter(requireContext(), reservations);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateReservations(reservations);
        }
    }
}