package com.example.realestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private final Context context;
    private final List<Property> properties;
    private final String userEmail;
    private final DatabaseHelper dbHelper;
    private boolean isFavorites;

    public PropertyAdapter(Context context, List<Property> properties, String userEmail, boolean isFavorites) {
        this.context = context;
        this.properties = properties;
        this.userEmail = userEmail;
        this.dbHelper = new DatabaseHelper(context);
        this.isFavorites = isFavorites;
    }


    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.title.setText(property.getTitle());
        holder.description.setText(property.getDescription());
        holder.price.setText(String.format("$%.2f", property.getPrice()));
        holder.location.setText(property.getLocation());

        String imageUrl = property.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageLoader.loadImage(imageUrl, holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }

        if (isFavorites) {
            holder.favoriteButton.setText("Remove");
            holder.favoriteButton.setOnClickListener(v -> {
                dbHelper.removeFromFavorites(userEmail, property.getId());
                properties.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.favoriteButton.setText("Favorite");
            holder.favoriteButton.setOnClickListener(v -> {
                boolean added = dbHelper.addToFavorites(userEmail, property.getId());
                Toast.makeText(context, added ? "Added to Favorites" : "Already in Favorites", Toast.LENGTH_SHORT).show();
            });
        }

        holder.reserveButton.setOnClickListener(v -> {
            ReservationDialogFragment dialog = ReservationDialogFragment.newInstance(property.getId(), userEmail);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "reservation_dialog");
        });
    }


    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void updateProperties(List<Property> newProperties) {
        properties.clear();
        properties.addAll(newProperties);
        notifyDataSetChanged();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, price, location;
        Button favoriteButton;
        Button reserveButton;

        PropertyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.property_image);
            title = itemView.findViewById(R.id.property_title);
            description = itemView.findViewById(R.id.property_description);
            price = itemView.findViewById(R.id.property_price);
            location = itemView.findViewById(R.id.property_location);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            reserveButton = itemView.findViewById(R.id.reserve_button);
        }
    }
}
