package com.example.realestate;

import android.os.AsyncTask;
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
            connection.connect();

            int code = connection.getResponseCode();
            if (code == 200) {
                return "SUCCESS";
            } else {
                return "FAIL";
            }
        } catch (Exception e) {
            e.printStackTrace(); // Important for debugging
            return "FAIL";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals("SUCCESS")) {
            activity.onConnectionSuccess();
        } else {
            activity.onConnectionFailed();
        }
    }
}
