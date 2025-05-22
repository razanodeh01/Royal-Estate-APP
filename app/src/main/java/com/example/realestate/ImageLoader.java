package com.example.realestate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private static final Map<String, Bitmap> imageCache = new HashMap<>();

    public static void loadImage(String url, ImageView imageView) {
        // Check cache first
        if (imageCache.containsKey(url)) {
            imageView.setImageBitmap(imageCache.get(url));
            return;
        }

        // Use placeholder while loading
        imageView.setImageResource(R.drawable.ic_launcher_background);

        // Start AsyncTask to download image
        new DownloadImageTask(imageView).execute(url);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;
        private String url;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            url = urls[0];
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                if (bitmap != null) {
                    imageCache.put(url, bitmap); // Cache the image
                }
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // Keep placeholder if image fails to load
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}