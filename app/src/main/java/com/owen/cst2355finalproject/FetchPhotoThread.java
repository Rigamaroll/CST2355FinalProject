package com.owen.cst2355finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class FetchPhotoThread implements Callable<ImageEntry> {

    String url;
    long id;

    public FetchPhotoThread(String url, long id) {

        this.url = url;
        this.id = id;

    }
    /**
     * calls each appropriate method to get the image information for displaying
     */
    @Override
    public ImageEntry call() {

        String explanation = null;
        String date = null;
        String title = null;
        String url = null;
        String hdUrl = null;
        String mediaType = null;
        Bitmap image = null;

        try {

            JSONObject jObject = getImageInfo(this.url);
            mediaType = jObject.getString("media_type");

            if (mediaType == null || !mediaType.contentEquals("image")) {

                return null;
            }

            explanation = jObject.getString("explanation");
            date = jObject.getString("date");
            title = jObject.getString("title");
            url = jObject.getString("url");
            hdUrl = jObject.getString("hdurl");
            image = getImageData(url);

        }catch (IOException | JSONException e) {

            e.printStackTrace();

        }

        ImageEntry theNewImage = new ImageEntry(id, title, url, date, hdUrl, explanation, image);
        return theNewImage;
    }

    /**
     * gets a new HttpURLConnection
     */
    private HttpURLConnection getConnection(String location) throws IOException {

        URL url = new URL(location);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        return urlConnection;
    }

    /**
     * Downloads the image from the URL provided.
     * @param imageURL the location of the image
     * @throws IOException
     */

    private Bitmap getImageData(String imageURL) throws IOException {

        Bitmap image = null;
        HttpURLConnection urlConnection = getConnection(imageURL);

        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {

            image = BitmapFactory.decodeStream(urlConnection.getInputStream());
        }

        return image;
    }

    /**
     * Downloads the JSON object containing the information about the image and its location.
     * It then sets the instance variables with the appropriate JSON fields.
     * @param imageURL location of the JSON object for the image information
     * @return
     * @throws IOException
     * @throws JSONException
     */

    private JSONObject getImageInfo(String imageURL) throws IOException, JSONException {

        HttpURLConnection urlConnection = getConnection(imageURL);
        JSONObject jObject = null;
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200) {
            InputStream inputStream = new URL(imageURL).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            String result = sb.toString();
            inputStream.close();
            jObject = new JSONObject(result);
        }
        return jObject;
    }

}
