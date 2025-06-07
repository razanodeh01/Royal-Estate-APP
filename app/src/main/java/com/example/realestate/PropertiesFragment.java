/**
 * Description:
 * PropertiesFragment is responsible for displaying and filtering real estate listings in the app.
 * It allows users to search by text, apply advanced filters (price range, type, and location), and browse properties with ease.
 */
package com.example.realestate;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PropertiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchInput;
    private ImageButton filterIcon;
    private DatabaseHelper dbHelper;
    private PropertyAdapter adapter;
    private String userEmail;
    private List<Property> allProperties = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_properties, container, false);

        recyclerView = view.findViewById(R.id.properties_recycler);
        searchInput = view.findViewById(R.id.search_bar);
        filterIcon = view.findViewById(R.id.filter_button_icon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(requireContext());
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

        adapter = new PropertyAdapter(requireContext(), new ArrayList<>(), userEmail, false);
        recyclerView.setAdapter(adapter);

        loadAllProperties();


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                filterProperties(
                        s.toString(),
                        null, null, null, null // or cached filter state if using it
                );
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        filterIcon.setOnClickListener(v -> {
            FilterFragment dialog = new FilterFragment();
            dialog.setOnFilterAppliedListener((min, max, loc, typ) -> {
                String searchQuery = searchInput.getText().toString().trim();
                filterProperties(searchQuery, min, max, loc, typ);
            });
            dialog.show(getParentFragmentManager(), "filter");
        });


        return view;
    }

    private void loadAllProperties() {
        allProperties.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM properties", null);
        if (cursor.moveToFirst()) {
            do {
                allProperties.add(new Property(
                        cursor.getInt(cursor.getColumnIndexOrThrow("property_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("area")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_url")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.updateProperties(new ArrayList<>(allProperties)); // Initial load
    }

    private void filterProperties(String queryText, String minPrice, String maxPrice, String location, String type) {
        List<Property> filtered = new ArrayList<>();

        String q = queryText != null ? queryText.toLowerCase() : "";
        boolean hasQuery = !q.isEmpty();
        boolean hasMinPrice = minPrice != null && !minPrice.isEmpty();
        boolean hasMaxPrice = maxPrice != null && !maxPrice.isEmpty();
        boolean hasLocation = location != null && !location.equalsIgnoreCase("Any");
        boolean hasType = type != null && !type.equalsIgnoreCase("Any");

        for (Property p : allProperties) {
            boolean matches = false;


            if (hasQuery && (
                    p.getTitle().toLowerCase().contains(q) ||
                            p.getLocation().toLowerCase().contains(q) ||
                            p.getType().toLowerCase().contains(q) ||
                            String.valueOf(p.getPrice()).contains(q))) {
                matches = true;
            }

            if (hasMinPrice) {
                try {
                    double min = Double.parseDouble(minPrice);
                    if (p.getPrice() >= min) matches = true;
                } catch (NumberFormatException ignored) {}
            }

            if (hasMaxPrice) {
                try {
                    double max = Double.parseDouble(maxPrice);
                    if (p.getPrice() <= max) matches = true;
                } catch (NumberFormatException ignored) {}
            }

            if (hasLocation && p.getLocation().equalsIgnoreCase(location)) {
                matches = true;
            }

            if (hasType && p.getType().equalsIgnoreCase(type)) {
                matches = true;
            }

            if (matches) {
                filtered.add(p);
            }
        }

        adapter.updateProperties(filtered);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
