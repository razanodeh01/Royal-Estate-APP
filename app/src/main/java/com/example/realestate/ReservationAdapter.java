package com.example.realestate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    private List<Reservation> reservations;

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        super(context, 0, reservations);
        this.reservations = reservations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservation, parent, false);
        }

        Reservation reservation = reservations.get(position);
        Property property = reservation.getProperty();

        TextView title = convertView.findViewById(R.id.property_title);
        TextView description = convertView.findViewById(R.id.property_description);
        TextView price = convertView.findViewById(R.id.property_price);
        TextView location = convertView.findViewById(R.id.property_location);
        TextView date = convertView.findViewById(R.id.reservation_date);
        ImageView image = convertView.findViewById(R.id.property_image);

        title.setText(property.getTitle());
        description.setText(property.getDescription());
        price.setText(String.format("$%.2f", property.getPrice()));
        location.setText(property.getLocation());
        date.setText("Reserved on: " + reservation.getReservationDate());

        // Load image using ImageLoader
        String imageUrl = property.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.contains("example.com")) {
            ImageLoader.loadImage(imageUrl, image);
        } else {
            image.setImageResource(R.drawable.ic_launcher_background);
        }

        return convertView;
    }

    public void updateReservations(List<Reservation> newReservations) {
        this.reservations.clear();
        this.reservations.addAll(newReservations);
        notifyDataSetChanged();
    }
}