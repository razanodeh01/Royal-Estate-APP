package com.example.realestate;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {

    private final MainActivity activity;

    public ConnectionAsyncTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0];
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return "FAIL";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (!result.equals("FAIL")) {
            activity.onConnectionSuccess(result);
        } else {
            activity.onConnectionFailed();
        }
    }
}