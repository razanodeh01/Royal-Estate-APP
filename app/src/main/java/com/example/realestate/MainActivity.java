package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button connectButton;
    String API_Url = "https://mocki.io/v1/070cc88d-4e9d-4eda-9e90-8059d2e05534";
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        connectButton = findViewById(R.id.button);
        dbHelper = new DatabaseHelper(this);

        connectButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
            new ConnectionAsyncTask(MainActivity.this).execute(API_Url);
        });
    }

    public void onConnectionSuccess(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray properties = jsonObject.getJSONArray("properties");
            for (int i = 0; i < properties.length(); i++) {
                JSONObject property = properties.getJSONObject(i);
                int id = property.getInt("id");
                String title = property.getString("title");
                String type = property.getString("type");
                double price = property.getDouble("price");
                String location = property.getString("location");
                String area = property.getString("area");
                int bedrooms = property.getInt("bedrooms");
                int bathrooms = property.getInt("bathrooms");
                String imageUrl = property.getString("image_url");
                String description = property.getString("description");

                dbHelper.insertProperty(id, title, type, price, location, area, bedrooms, bathrooms, imageUrl, description);
            }
            Toast.makeText(this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            onConnectionFailed();
        }
    }

    public void onConnectionFailed() {
        Toast.makeText(this, "Connection Failed. Please try again.", Toast.LENGTH_LONG).show();
    }
}