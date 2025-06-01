package com.example.realestate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffersFragment extends Fragment {

    private ListView propertiesListView;
    private List<Property> propertiesList;
    private PropertyAdapter propertyAdapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_special_offers, container, false);

        propertiesListView = view.findViewById(R.id.properties_list);
        propertiesList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(requireContext(), propertiesList, this::updateSpecialOffer);
        propertiesListView.setAdapter(propertyAdapter);
        dbHelper = new DatabaseHelper(requireContext());

        loadProperties();

        return view;
    }

    private void loadProperties() {
        propertiesList.clear();
        Cursor cursor = dbHelper.getAllProperties();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int isSpecialOffer = cursor.getInt(cursor.getColumnIndexOrThrow("is_special_offer"));
            propertiesList.add(new Property(id, title, isSpecialOffer == 1));
        }
        cursor.close();
        propertyAdapter.notifyDataSetChanged();
    }

    private void updateSpecialOffer(int propertyId, boolean isSpecialOffer) {
        if (dbHelper.setSpecialOffer(propertyId, isSpecialOffer)) {
            Toast.makeText(requireContext(), "Special offer updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update special offer", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper class for property data
    static class Property {
        int id;
        String title;
        boolean isSpecialOffer;

        Property(int id, String title, boolean isSpecialOffer) {
            this.id = id;
            this.title = title;
            this.isSpecialOffer = isSpecialOffer;
        }
    }

    // Adapter for property list
    static class PropertyAdapter extends ArrayAdapter<Property> {
        private final Context context;
        private final List<Property> properties;
        private final SpecialOfferCallback callback;

        interface SpecialOfferCallback {
            void updateSpecialOffer(int propertyId, boolean isSpecialOffer);
        }

        PropertyAdapter(Context context, List<Property> properties, SpecialOfferCallback callback) {
            super(context, 0, properties);
            this.context = context;
            this.properties = properties;
            this.callback = callback;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
            }

            Property property = getItem(position);
            TextView titleText = convertView.findViewById(R.id.property_title);
            CheckBox specialOfferCheckBox = convertView.findViewById(R.id.special_offer_checkbox);

            titleText.setText(property.title);
            specialOfferCheckBox.setChecked(property.isSpecialOffer);
            specialOfferCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                callback.updateSpecialOffer(property.id, isChecked);
                property.isSpecialOffer = isChecked;
            });

            return convertView;
        }
    }
}