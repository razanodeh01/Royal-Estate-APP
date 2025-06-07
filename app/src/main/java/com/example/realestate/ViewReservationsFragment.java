/**
 * Description:
 * This fragment provides the admin with a complete overview of
 * all property reservations made by users in the system.
 */

package com.example.realestate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class ViewReservationsFragment extends Fragment {

    private ListView reservationsListView;
    private List<Reservation> reservationsList;
    private ReservationAdapter reservationAdapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_reservations, container, false);

        reservationsListView = view.findViewById(R.id.reservations_list);
        reservationsList = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(requireContext(), reservationsList);
        reservationsListView.setAdapter(reservationAdapter);
        dbHelper = new DatabaseHelper(requireContext());

        loadReservations();

        return view;
    }

    private void loadReservations() {
        reservationsList.clear();
        Cursor cursor = dbHelper.getAllReservations();
        while (cursor.moveToNext()) {
            int reservationId = cursor.getInt(cursor.getColumnIndexOrThrow("reservation_id"));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
            int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("reservation_date"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            String propertyTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            reservationsList.add(new Reservation(reservationId, userEmail, propertyId, date, propertyTitle, firstName + " " + lastName));
        }
        cursor.close();
        reservationAdapter.notifyDataSetChanged();
    }


    static class Reservation {
        int id, propertyId;
        String userEmail, date, propertyTitle, customerName;

        Reservation(int id, String userEmail, int propertyId, String date, String propertyTitle, String customerName) {
            this.id = id;
            this.userEmail = userEmail;
            this.propertyId = propertyId;
            this.date = date;
            this.propertyTitle = propertyTitle;
            this.customerName = customerName;
        }
    }


    static class ReservationAdapter extends ArrayAdapter<Reservation> {
        private final Context context;
        private final List<Reservation> reservations;

        ReservationAdapter(Context context, List<Reservation> reservations) {
            super(context, 0, reservations);
            this.context = context;
            this.reservations = reservations;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.reservation_item, parent, false);
            }

            Reservation reservation = getItem(position);
            TextView textView = convertView.findViewById(R.id.reservation_info);
            textView.setText(String.format("Property ID: %d\n\nCustomer name: %s\n\nProperty type: %s\n\nDate: %s",
                    reservation.id, reservation.customerName, reservation.propertyTitle, reservation.date));

            return convertView;
        }
    }
}