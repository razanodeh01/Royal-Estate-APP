package com.example.realestate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

public class PropertyAdapter extends ArrayAdapter<Property> {

    private List<Property> properties;
    private String userEmail;
    private DatabaseHelper dbHelper;
    private boolean isFavorites;

    public PropertyAdapter(Context context, List<Property> properties, String userEmail, boolean isFavorites) {
        super(context, 0, properties);
        this.properties = properties;
        this.userEmail = userEmail;
        this.dbHelper = new DatabaseHelper(context);
        this.isFavorites = isFavorites;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    isFavorites ? R.layout.item_favorite : R.layout.item_property, parent, false);
        }

        Property property = properties.get(position);

        TextView title = convertView.findViewById(R.id.property_title);
        TextView description = convertView.findViewById(R.id.property_description);
        TextView price = convertView.findViewById(R.id.property_price);
        TextView location = convertView.findViewById(R.id.property_location);
        ImageView image = convertView.findViewById(R.id.property_image);
        Button favoriteButton = isFavorites ? null : convertView.findViewById(R.id.favorite_button);
        Button reserveButton = convertView.findViewById(R.id.reserve_button);
        Button removeButton = isFavorites ? convertView.findViewById(R.id.remove_button) : null;

        title.setText(property.getTitle());
        description.setText(property.getDescription());
        price.setText(String.format("$%.2f", property.getPrice()));
        location.setText(property.getLocation());

        // Load image using ImageLoader
        String imageUrl = property.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.contains("example.com")) {
            ImageLoader.loadImage(imageUrl, image);
        } else {
            image.setImageResource(R.drawable.ic_launcher_background);
        }

        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(v -> {
                boolean added = dbHelper.addToFavorites(userEmail, property.getId());
                Toast.makeText(getContext(), added ? "Added to Favorites" : "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
            });
        }

        if (reserveButton != null) {
            reserveButton.setOnClickListener(v -> {
                ReservationFragment fragment = new ReservationFragment();
                Bundle args = new Bundle();
                args.putInt("property_id", property.getId());
                args.putString("user_email", userEmail);
                fragment.setArguments(args);
                ((FragmentActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_content, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        if (removeButton != null) {
            removeButton.setOnClickListener(v -> {
                boolean removed = dbHelper.removeFromFavorites(userEmail, property.getId());
                if (removed) {
                    properties.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }

    public void updateProperties(List<Property> newProperties) {
        this.properties.clear();
        this.properties.addAll(newProperties);
        notifyDataSetChanged();
    }
}