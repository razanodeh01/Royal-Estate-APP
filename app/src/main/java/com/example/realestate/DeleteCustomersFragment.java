/**
 * Description:
 * The `DeleteCustomersFragment` provides an administrative interface that allows admins to
 * view and delete registered customer accounts from the system.
 * It displays a list of users and includes a delete button for each entry, enabling quick account removal with real-time updates.
 */

package com.example.realestate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class DeleteCustomersFragment extends Fragment {

    private ListView usersListView;
    private ArrayList<User> usersList;
    private UserAdapter userAdapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_customers, container, false);

        usersListView = view.findViewById(R.id.users_list);
        usersList = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(), usersList, this::deleteUser);
        usersListView.setAdapter(userAdapter);
        dbHelper = new DatabaseHelper(requireContext());

        loadUsers();

        return view;
    }

    private void loadUsers() {
        usersList.clear();
        Cursor cursor = dbHelper.getAllUsers();
        while (cursor.moveToNext()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
            usersList.add(new User(email, firstName, lastName));
        }
        cursor.close();
        userAdapter.notifyDataSetChanged();
    }

    private void deleteUser(String email) {
        if (dbHelper.deleteUser(email)) {
            Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }


    static class User {
        String email, firstName, lastName;

        User(String email, String firstName, String lastName) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }


    static class UserAdapter extends ArrayAdapter<User> {
        private final Context context;
        private final List<User> users;
        private final DeleteCallback callback;

        interface DeleteCallback {
            void deleteUser(String email);
        }

        UserAdapter(Context context, List<User> users, DeleteCallback callback) {
            super(context, 0, users);
            this.context = context;
            this.users = users;
            this.callback = callback;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
            }

            User user = getItem(position);
            TextView emailText = convertView.findViewById(R.id.user_email);
            Button deleteButton = convertView.findViewById(R.id.delete_button);

            emailText.setText("Customer name: "+ user.firstName + " " + user.lastName);
            deleteButton.setOnClickListener(v -> callback.deleteUser(user.email));

            return convertView;
        }
    }
}