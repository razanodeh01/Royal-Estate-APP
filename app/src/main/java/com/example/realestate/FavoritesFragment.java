package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView listView;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private PropertyAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        listView = view.findViewById(R.id.favorites_list);
        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        List<Property> properties = new ArrayList<>();
        Cursor cursor = dbHelper.getFavoritesByUser(userEmail);
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

                properties.add(new Property(id, title, type, price, location, area, bedrooms, bathrooms, imageUrl, description));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (adapter == null) {
            adapter = new PropertyAdapter(requireContext(), properties, userEmail, true);
            listView.setLayoutManager(new LinearLayoutManager(requireContext()));

        } else {
            adapter.updateProperties(properties);
        }
    }
}