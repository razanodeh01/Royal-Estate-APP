package com.example.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button connectButton;
    String AAPI_Url = "https://mocki.io/v1/f208b41b-12f1-45d0-9b74-2a635f184a2d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        connectButton = findViewById(R.id.button);

        connectButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
            new ConnectionAsyncTask(MainActivity.this).execute(AAPI_Url);
        });
    }

    public void onConnectionSuccess() {
        // Optional: Show a success toast
        Toast.makeText(this, "Connected Successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to login/register screen
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

    public void onConnectionFailed() {
        Toast.makeText(this, "Connection Failed. Please try again.", Toast.LENGTH_LONG).show();
    }
}
