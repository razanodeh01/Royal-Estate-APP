package com.example.realestate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class PropertyFeaturedAdapter extends RecyclerView.Adapter<PropertyFeaturedAdapter.ViewHolder> {

    private final Context context;
    private final List<FeaturedFragment.Property> properties;
    private final ReserveCallback reserveCallback;
    private final FavoriteCallback favoriteCallback;

    public interface ReserveCallback {
        void onReserve(int propertyId);
    }

    public interface FavoriteCallback {
        void onFavorite(int propertyId, boolean isFavorite);
    }

    public PropertyFeaturedAdapter(Context context, List<FeaturedFragment.Property> properties,
                                   ReserveCallback reserveCallback, FavoriteCallback favoriteCallback) {
        this.context = context;
        this.properties = properties;
        this.reserveCallback = reserveCallback;
        this.favoriteCallback = favoriteCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_featured_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeaturedFragment.Property property = properties.get(position);

        holder.titleText.setText(property.title);
        holder.detailsText.setText(String.format("Type: %s\nPrice: $%.2f\nLocation: %s\nArea: %s\nBedrooms: %d\nBathrooms: %d",
                property.type, property.price, property.location, property.area, property.bedrooms, property.bathrooms));

        if (property.imageUrl != null && !property.imageUrl.isEmpty()) {
            ImageLoader.loadImage(property.imageUrl, holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.reserveButton.setOnClickListener(v -> reserveCallback.onReserve(property.id));

        holder.favoriteButton.setText(property.isFavorite ? "Remove" : "Favorite");
        holder.favoriteButton.setIconResource(property.isFavorite ? R.drawable.ic_favourite_filled : R.drawable.ic_fav_item);
        holder.favoriteButton.setIconTint(null); // Clear tint to respect drawable colors
        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteState = !property.isFavorite;
            favoriteCallback.onFavorite(property.id, newFavoriteState);
            // Update button state immediately
            property.isFavorite = newFavoriteState;
            holder.favoriteButton.setText(newFavoriteState ? "Remove" : "Favorite");
            holder.favoriteButton.setIconResource(newFavoriteState ? R.drawable.ic_favourite_filled : R.drawable.ic_fav_item);
            // Run animation only when adding to favorites
            if (newFavoriteState) {
                AnimatorSet animatorSet = new AnimatorSet();
                // Scale animation
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.favoriteButton, "scaleX", 1f, 1.5f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.favoriteButton, "scaleY", 1f, 1.5f, 1f);
                scaleX.setDuration(600);
                scaleY.setDuration(600);
                // Icon tint animation
                ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                        Color.parseColor("#d9b65b"), Color.parseColor("#FF0000"));
                colorAnimator.addUpdateListener(animation -> {
                    holder.favoriteButton.getIcon().setTint((int) animation.getAnimatedValue());
                });
                colorAnimator.setDuration(600);
                // Play together
                animatorSet.playTogether(scaleX, scaleY, colorAnimator);
                animatorSet.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, detailsText;
        MaterialButton reserveButton, favoriteButton;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.property_title);
            detailsText = itemView.findViewById(R.id.property_details);
            reserveButton = itemView.findViewById(R.id.reserve_button);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            imageView = itemView.findViewById(R.id.property_image);
        }
    }
}