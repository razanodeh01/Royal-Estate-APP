package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertiesFragment extends Fragment {

    private ListView listView;
    private Spinner typeSpinner;
    private EditText locationEdit, minPriceEdit, maxPriceEdit;
    private Button filterButton;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private PropertyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_properties, container, false);

        listView = view.findViewById(R.id.properties_list);
        typeSpinner = view.findViewById(R.id.spinner_type);
        locationEdit = view.findViewById(R.id.search_location);
        minPriceEdit = view.findViewById(R.id.min_price);
        maxPriceEdit = view.findViewById(R.id.max_price);
        filterButton = view.findViewById(R.id.filter_button);
        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        if (userEmail == null) {
            // Handle missing email (e.g., show error or redirect)
        }

        // Populate spinner
        List<String> types = Arrays.asList("All", "Apartment", "Villa", "Land");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Load initial properties
        loadProperties(null, null, null, null);

        // Filter button
        filterButton.setOnClickListener(v -> {
            String type = typeSpinner.getSelectedItem().toString();
            String location = locationEdit.getText().toString().trim();
            String minPrice = minPriceEdit.getText().toString().trim();
            String maxPrice = maxPriceEdit.getText().toString().trim();
            loadProperties(type.equals("All") ? null : type, location, minPrice, maxPrice);
        });

        return view;
    }

    private void loadProperties(String type, String location, String minPrice, String maxPrice) {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT * FROM properties";
        String[] selectionArgs = null;
        if (type != null) {
            query += " WHERE type=?";
            selectionArgs = new String[]{type};
        }
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String propType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String loc = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String area = cursor.getString(cursor.getColumnIndexOrThrow("area"));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms"));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                boolean matches = true;
                if (location != null && !location.isEmpty() && !loc.toLowerCase().contains(location.toLowerCase())) {
                    matches = false;
                }
                if (minPrice != null && !minPrice.isEmpty()) {
                    try {
                        if (price < Double.parseDouble(minPrice)) {
                            matches = false;
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid input (e.g., skip filter or show error)
                    }
                }
                if (maxPrice != null && !maxPrice.isEmpty()) {
                    try {
                        if (price > Double.parseDouble(maxPrice)) {
                            matches = false;
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid input (e.g., skip filter or show error)
                    }
                }

                if (matches) {
                    properties.add(new Property(id, title, propType, price, loc, area, bedrooms, bathrooms, imageUrl, description));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (adapter == null) {
            adapter = new PropertyAdapter(requireContext(), properties, userEmail, false);
            listView.setAdapter(adapter);
        } else {
            adapter.updateProperties(properties);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}