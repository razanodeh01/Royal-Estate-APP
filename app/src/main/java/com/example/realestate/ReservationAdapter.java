package com.example.realestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private final List<Reservation> reservations;
    private final Context context;

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        Property property = reservation.getProperty();

        holder.title.setText(property.getTitle());
        holder.description.setText(property.getDescription());
        holder.price.setText(String.format("$%.2f", property.getPrice()));
        holder.location.setText(property.getLocation());
        holder.date.setText("Reserved on: " + reservation.getReservationDate());

        String imageUrl = property.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.contains("example.com")) {
            ImageLoader.loadImage(imageUrl, holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void updateReservations(List<Reservation> newReservations) {
        reservations.clear();
        reservations.addAll(newReservations);
        notifyDataSetChanged();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, price, location, date;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.property_image);
            title = itemView.findViewById(R.id.property_title);
            description = itemView.findViewById(R.id.property_description);
            price = itemView.findViewById(R.id.property_price);
            location = itemView.findViewById(R.id.property_location);
            date = itemView.findViewById(R.id.reservation_date);
        }
    }
}
