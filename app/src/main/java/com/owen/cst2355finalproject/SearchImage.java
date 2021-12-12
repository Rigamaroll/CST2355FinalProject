package com.owen.cst2355finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SearchImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);

        Button searchDate = findViewById(R.id.searchImageButton);
        Button saveImage = findViewById(R.id.saveImageButton);
        ProgressBar imageProgress = findViewById(R.id.downloadProgress);

        DatePickerDialog datePicker = getDatePickerDialog(new DatePickerListener());
        datePicker.setTitle(R.string.pickerTitle);


        searchDate.setOnClickListener((click) -> {

            datePicker.show();
            imageProgress.setVisibility(ProgressBar.VISIBLE);

        });

        saveImage.setOnClickListener((click) -> {



        });
    }

    private DatePickerDialog getDatePickerDialog (DatePickerDialog.OnDateSetListener listen) {

        Calendar cal = Calendar.getInstance();
        return new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

    }

    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {

            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            DateFormat imageDate = new SimpleDateFormat("yyyy-MM-dd");
            String newDate = imageDate.format(cal.getTime());
            ImageQuery request = new ImageQuery();
            request.execute("https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=" + newDate);

        }
    }

    private class ImageQuery extends AsyncTask<String, Integer, String> {

        String title;
        String date;
        String url;
        String hdUrl;

        /*
         *gets a new HttpURLConnection
         */
        private HttpURLConnection getConnection(String location) throws IOException {

            URL url = new URL(location);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection;
        }

        /*
         *calls each appropriate method to get the correct information for the weather displaying app
         */
        @Override
        protected String doInBackground(String... args) {

            try {

                getImageInfo(args[0]);
                publishProgress(100);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            ProgressBar progressBar = findViewById(R.id.progressWeather);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            TextView imageTitle = findViewById(R.id.imageTitle);
            TextView imageDate = findViewById(R.id.imageDate);
            TextView imageUrl = findViewById(R.id.imageURL);
            TextView imageHDUrl = findViewById(R.id.imageHdURL);
            ImageView newImage = findViewById(R.id.newImageView);

            currentTemp.append(this.currTemp);
            miniTemp.append(this.minTemp);
            maxiTemp.append(this.maxTemp);
            currWeather.setImageBitmap(this.currWeather);
            uvIndex.append(this.uv);

            ProgressBar progress = findViewById(R.id.progressWeather);
            progress.setVisibility(View.INVISIBLE);
            super.onPostExecute(s);
        }

        private void getImageInfo(String urlUv) throws IOException, JSONException {

            HttpURLConnection urlConnection = getConnection(urlUv);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = new URL(urlUv).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                inputStream.close();
                JSONObject jObject = new JSONObject(result);
                this.uv = String.valueOf(jObject.getDouble("value"));
            }
        }
    }
}