package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FeaturedFragment extends Fragment {

    private ListView propertiesListView;
    private List<Property> propertiesList;
    private PropertyFeaturedAdapter adapter;
    private DatabaseHelper dbHelper;
    private String userEmail;

    public FeaturedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get user email from HomeActivity
        if (getActivity() != null && getActivity().getIntent() != null) {
            userEmail = getActivity().getIntent().getStringExtra("user_email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured, container, false);

        propertiesListView = view.findViewById(R.id.featured_properties_list);
        propertiesList = new ArrayList<>();
        adapter = new PropertyFeaturedAdapter(requireContext(), propertiesList, this::reserveProperty, this::toggleFavorite);
        propertiesListView.setAdapter(adapter);
        dbHelper = new DatabaseHelper(requireContext());

        loadSpecialOffers();

        return view;
    }

    private void loadSpecialOffers() {
        propertiesList.clear();
        Cursor cursor = dbHelper.getSpecialOfferProperties();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String area = cursor.getString(cursor.getColumnIndexOrThrow("area"));
            int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms"));
            int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms"));
            boolean isFavorite = dbHelper.isPropertyFavorited(userEmail, id);
            propertiesList.add(new Property(id, title, type, price, location, area, bedrooms, bathrooms, isFavorite));
        }
        cursor.close();
        adapter.notifyDataSetChanged();

        if (propertiesList.isEmpty()) {
            Toast.makeText(requireContext(), "No special offers available", Toast.LENGTH_SHORT).show();
        }
    }

    private void reserveProperty(int propertyId) {
        if (userEmail == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use current date for simplicity; adjust as needed
        String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        boolean success = dbHelper.addReservation(userEmail, propertyId, dateTime);
        Toast.makeText(requireContext(), success ? "Property reserved" : "Property already reserved", Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite(int propertyId, boolean isFavorite) {
        if (userEmail == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (isFavorite) {
            success = dbHelper.addToFavorites(userEmail, propertyId);
            Toast.makeText(requireContext(), success ? "Added to favorites" : "Failed to add to favorites", Toast.LENGTH_SHORT).show();
        } else {
            success = dbHelper.removeFromFavorites(userEmail, propertyId);
            Toast.makeText(requireContext(), success ? "Removed from favorites" : "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
        }

        // Refresh list to update favorite status
        loadSpecialOffers();
    }

    // Helper class for property data
    static class Property {
        int id, bedrooms, bathrooms;
        String title, type, location, area;
        double price;
        boolean isFavorite;

        Property(int id, String title, String type, double price, String location, String area, int bedrooms, int bathrooms, boolean isFavorite) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.price = price;
            this.location = location;
            this.area = area;
            this.bedrooms = bedrooms;
            this.bathrooms = bathrooms;
            this.isFavorite = isFavorite;
        }
    }
}