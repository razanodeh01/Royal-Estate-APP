package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button connectButton;
    String apiUrl = "https://mocki.io/v1/f208b41b-12f1-45d0-9b74-2a635f184a2d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        connectButton = findViewById(R.id.button);

        connectButton.setOnClickListener(view -> {
            new ConnectionAsyncTask(MainActivity.this).execute(apiUrl);
        });
    }

    public void onConnectionSuccess() {
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

    public void onConnectionFailed() {
        Toast.makeText(this, "Connection Failed. Please try again.", Toast.LENGTH_LONG).show();
    }
}