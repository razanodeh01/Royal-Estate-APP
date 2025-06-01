package com.example.realestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PropertyFeaturedAdapter extends ArrayAdapter<FeaturedFragment.Property> {

    private final Context context;
    private final List<FeaturedFragment.Property> properties;
    private final ReserveCallback reserveCallback;
    private final FavoriteCallback favoriteCallback;

    interface ReserveCallback {
        void onReserve(int propertyId);
    }

    interface FavoriteCallback {
        void onFavorite(int propertyId, boolean isFavorite);
    }

    public PropertyFeaturedAdapter(Context context, List<FeaturedFragment.Property> properties,
                                   ReserveCallback reserveCallback, FavoriteCallback favoriteCallback) {
        super(context, 0, properties);
        this.context = context;
        this.properties = properties;
        this.reserveCallback = reserveCallback;
        this.favoriteCallback = favoriteCallback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.property_featured_item, parent, false);
        }

        FeaturedFragment.Property property = getItem(position);

        TextView titleText = convertView.findViewById(R.id.property_title);
        TextView detailsText = convertView.findViewById(R.id.property_details);
        Button reserveButton = convertView.findViewById(R.id.reserve_button);
        Button favoriteButton = convertView.findViewById(R.id.favorite_button);

        titleText.setText(property.title);
        detailsText.setText(String.format("Type: %s\nPrice: $%.2f\nLocation: %s\nArea: %s\nBedrooms: %d\nBathrooms: %d",
                property.type, property.price, property.location, property.area, property.bedrooms, property.bathrooms));

        reserveButton.setOnClickListener(v -> reserveCallback.onReserve(property.id));

        favoriteButton.setText(property.isFavorite ? "Unfavorite" : "Favorite");
        favoriteButton.setOnClickListener(v -> favoriteCallback.onFavorite(property.id, !property.isFavorite));

        return convertView;
    }
}