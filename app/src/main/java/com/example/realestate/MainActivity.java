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
    String API_Url = "https://mocki.io/v1/5b613482-4ae2-4cf1-9ff8-0b340c40cd9b";
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

                if (imageUrl.contains("example.com")) {
                    switch (id) {
                        case 104:
                            imageUrl = "https://i.imgur.com/5rdiVsm.png"; break;
                        case 105:
                            imageUrl = "https://i.imgur.com/gA7pFEA.png"; break;
                        case 106:
                            imageUrl = "https://i.imgur.com/lHEjs1U.png"; break;
                        case 107:
                            imageUrl = "https://i.imgur.com/5cb3eMw.png"; break;
                        case 108:
                            imageUrl = "https://i.imgur.com/hG8rws6.png"; break;
                        case 109:
                            imageUrl = "https://i.imgur.com/MaicmDu.png"; break;
                        case 110:
                            imageUrl = "https://i.imgur.com/9tryWcf.png"; break;
                        case 111:
                            imageUrl = "https://i.imgur.com/2UCtvcm.png"; break;
                        case 112:
                            imageUrl = "https://i.imgur.com/dHqKVlO.png"; break;
                        case 113:
                            imageUrl = "https://i.imgur.com/JCZ1ChI.png"; break;
                        case 114:
                            imageUrl = "https://i.imgur.com/2xA4Xq2.png"; break;
                        case 115:
                            imageUrl = "https://i.imgur.com/xNhlAmk.png"; break;
                        case 116:
                            imageUrl = "https://i.imgur.com/DdE9lrN.png"; break;
                        case 117:
                            imageUrl = "https://i.imgur.com/hDX2jIR.png"; break;
                        case 118:
                            imageUrl = "https://i.imgur.com/VP7iuw3.png"; break;
                        case 119:
                            imageUrl = "https://i.imgur.com/FTeSQTA.png"; break;
                        case 120:
                            imageUrl = "https://i.imgur.com/6Z2jhOO.png"; break;
                        default:
                            imageUrl = "https://i.imgur.com/Fi2nBVB.png"; // fallback
                    }
                }


                dbHelper.insertProperty(id, title, type, price, location, area, bedrooms, bathrooms, imageUrl, description);
            }
            Toast.makeText(this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginRegisterActivity.class);
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