/**
 * Description:
 * PropertyAdapter is a custom RecyclerView adapter used to display and manage property listings in the app.
 * It serves as a bridge between the list of Property objects and the UI components shown in a scrolling list format.
 */

package com.example.realestate;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private static final String TAG = "PropertyAdapter";
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
            holder.favoriteButton.setIconResource(R.drawable.ic_favourite_filled);
            holder.favoriteButton.setIconTint(null);
            holder.favoriteButton.setOnClickListener(v -> {
                dbHelper.removeFromFavorites(userEmail, property.getId());
                properties.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            boolean isFavorited = dbHelper.isPropertyFavorited(userEmail, property.getId());
            holder.favoriteButton.setText(isFavorited ? "Remove" : "Favorite");
            holder.favoriteButton.setIconResource(isFavorited ? R.drawable.ic_favourite_filled : R.drawable.ic_fav_item);
            holder.favoriteButton.setIconTint(null);
            holder.favoriteButton.setOnClickListener(v -> {
                boolean newFavoriteState = !isFavorited;
                holder.favoriteButton.setEnabled(false); // Prevent rapid clicks
                toggleFavorite(property, position, holder.favoriteButton, newFavoriteState);
            });
        }

        holder.reserveButton.setOnClickListener(v -> {
            ReservationDialogFragment dialog = ReservationDialogFragment.newInstance(property.getId(), userEmail);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "reservation_dialog");
        });
    }

    private void toggleFavorite(Property property, int position, MaterialButton favoriteButton, boolean newFavoriteState) {
        Log.d(TAG, "Toggle favorite: propertyId=" + property.getId() + ", newState=" + newFavoriteState);
        boolean success;
        if (newFavoriteState) {
            success = dbHelper.addToFavorites(userEmail, property.getId());
            if (success) {
                favoriteButton.setText("Remove");
                favoriteButton.setIconResource(R.drawable.ic_favourite_filled);
                favoriteButton.setIconTint(null);

                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(favoriteButton, "scaleX", 1f, 1.5f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(favoriteButton, "scaleY", 1f, 1.5f, 1f);
                scaleX.setDuration(600);
                scaleY.setDuration(600);
                ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                        Color.parseColor("#d9b65b"), Color.parseColor("#FF0000"));
                colorAnimator.addUpdateListener(animation -> {
                    favoriteButton.getIcon().setTint((int) animation.getAnimatedValue());
                });
                colorAnimator.setDuration(600);
                animatorSet.playTogether(scaleX, scaleY, colorAnimator);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.d(TAG, "Animation started for propertyId=" + property.getId());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.d(TAG, "Animation ended for propertyId=" + property.getId());
                        favoriteButton.setEnabled(true);
                        notifyItemChanged(position); // Notify after animation
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.d(TAG, "Animation cancelled for propertyId=" + property.getId());
                        favoriteButton.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                animatorSet.start();
            } else {
                favoriteButton.setEnabled(true);
                Toast.makeText(context, "Failed to Add", Toast.LENGTH_SHORT).show();
            }
        } else {
            success = dbHelper.removeFromFavorites(userEmail, property.getId());
            if (success) {
                favoriteButton.setText("Favorite");
                favoriteButton.setIconResource(R.drawable.ic_fav_item);
                favoriteButton.setIconTint(null);
                favoriteButton.setEnabled(true);
                notifyItemChanged(position);
                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                favoriteButton.setEnabled(true);
                Toast.makeText(context, "Failed to Remove", Toast.LENGTH_SHORT).show();
            }
        }
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
        MaterialButton favoriteButton;
        MaterialButton reserveButton;

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