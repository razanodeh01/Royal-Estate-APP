package com.example.realestate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PropertyFeaturedAdapter extends RecyclerView.Adapter<PropertyFeaturedAdapter.ViewHolder> {

    private final Context context;
    private final List<FeaturedFragment.Property> properties;
    private final ReserveCallback reserveCallback;
    private final FavoriteCallback favoriteCallback;
    ImageView imageView;

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
            ImageLoader.loadImage(property.imageUrl, holder.imageView); // Use your helper
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background); // fallback image
        }

        holder.reserveButton.setOnClickListener(v -> reserveCallback.onReserve(property.id));

        holder.favoriteButton.setText(property.isFavorite ? "Unfavorite" : "Favorite");
        holder.favoriteButton.setOnClickListener(v -> favoriteCallback.onFavorite(property.id, !property.isFavorite));
    }


    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, detailsText;
        Button reserveButton, favoriteButton;
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
