package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

public class StatisticsFragment extends Fragment {

    private TextView usersCountText, reservationsCountText, countriesText, genderText;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        usersCountText = view.findViewById(R.id.users_count);
        reservationsCountText = view.findViewById(R.id.reservations_count);
        countriesText = view.findViewById(R.id.top_countries);
        genderText = view.findViewById(R.id.gender_distribution);
        dbHelper = new DatabaseHelper(requireContext());

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        // User count
        usersCountText.setText(String.format("Total Users: %d", dbHelper.getUserCount()));

        // Reservation count
        reservationsCountText.setText(String.format("Total Reservations: %d", dbHelper.getReservationCount()));

        // Top countries
        StringBuilder countriesBuilder = new StringBuilder("Most Reserving Countries:\n");
        Cursor countryCursor = dbHelper.getTopReservingCountries();
        while (countryCursor.moveToNext()) {
            String country = countryCursor.getString(countryCursor.getColumnIndexOrThrow("country"));
            int count = countryCursor.getInt(countryCursor.getColumnIndexOrThrow("reservation_count"));
            countriesBuilder.append(country).append(": ").append(count).append("\n");
        }
        countryCursor.close();
        countriesText.setText(countriesBuilder.toString());

        // Gender distribution
        Cursor genderCursor = dbHelper.getGenderDistribution();
        long total = dbHelper.getUserCount();
        StringBuilder genderBuilder = new StringBuilder("Gender Distribution:\n");
        DecimalFormat df = new DecimalFormat("0.00%");
        while (genderCursor.moveToNext()) {
            String gender = genderCursor.getString(genderCursor.getColumnIndexOrThrow("gender"));
            int count = genderCursor.getInt(genderCursor.getColumnIndexOrThrow("count"));
            double percentage = total > 0 ? (double) count / total : 0.0;
            genderBuilder.append(gender).append(": ").append(df.format(percentage)).append("\n");
        }
        genderCursor.close();
        genderText.setText(genderBuilder.toString());
    }
}