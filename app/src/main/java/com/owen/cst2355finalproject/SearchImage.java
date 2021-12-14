package com.owen.cst2355finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SearchImage extends AppCompatActivity {

    SQLiteDatabase imageDB;
    DatePickerDialog datePicker;
    MainToolBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);

        NavigationView navView = findViewById(R.id.navView);
        Toolbar tools = findViewById(R.id.mainToolBar);
        DrawerLayout drawer = findViewById(R.id.navDrawer);
        //toolbar = new MainToolBar(this, this, tools, drawer, navView);
        toolbar.getToolbar().setTitle(R.string.searchImageTitle);

        Button searchDate = findViewById(R.id.searchImageButton);
        Button saveImage = findViewById(R.id.saveImageButton);

        TextView hdURL = findViewById(R.id.searchImageHdURL);
        hdURL.setOnClickListener((click) -> {

            Uri imageLocation = Uri.parse(hdURL.getText().toString());
            Intent goBrowser = new Intent(Intent.ACTION_VIEW, imageLocation);
            startActivity(goBrowser);

        });

        datePicker = getDatePickerDialog(new DatePickerListener());
        datePicker.setTitle(R.string.pickerTitle);

        searchDate.setOnClickListener((click) -> {

            datePicker.show();

        });

        saveImage.setOnClickListener((click) -> {

            try {
                addToDB();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
    }

    private void alertDate(String reason) {
        String alertString = null;
        switch (reason) {
            case "past":
                alertString = getString(R.string.dateRangePast);
                break;
            case "future":
                alertString = getString(R.string.dateRangeFuture);
                break;
            case "video":
                alertString = getString(R.string.dateRangeVideo);
                break;
            default:
                break;
        }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.dateOutRange)
                    .setMessage(alertString)
                    .setPositiveButton(R.string.yes, (click, arg) -> {
                        datePicker.show();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> {
                    })
                    .create()
                    .show();
    }

    private void addToDB() throws MalformedURLException {

        ImageDbOpener dbOpener = new ImageDbOpener(this);
        imageDB = dbOpener.getWritableDatabase();

        TextView imageTitle = findViewById(R.id.searchImageName);
        TextView imageDate = findViewById(R.id.searchImageDate);
        TextView imageUrl = findViewById(R.id.searchImageURL);
        TextView imageHDUrl = findViewById(R.id.searchImageHdURL);
        TextView imageExplanation = findViewById(R.id.searchImageExplanation);
        ImageView imageImage =findViewById(R.id.searchImageView);

        String title = String.valueOf(imageTitle.getText());
        String date = String.valueOf(imageDate.getText());
        String url = String.valueOf(imageUrl.getText());
        String hdUrl = String.valueOf(imageHDUrl.getText());
        String explanation = String.valueOf(imageExplanation.getText());
        BitmapDrawable theImage = (BitmapDrawable) imageImage.getDrawable();
        Bitmap newImage = theImage.getBitmap();

        long id = 0;
        ContentValues newRow = new ContentValues();
        Cursor results = imageDB.rawQuery("SELECT max(seq) FROM sqlite_sequence;", null, null);
        while (results.moveToNext()) {

            id = results.getLong(0) + 1;
        }
        if (results != null) {
            results.close();
        }
        ImageEntry imageEntry = new ImageEntry(id, title, url, date, hdUrl, explanation, newImage);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(bytesOut);
            objOut.writeObject(imageEntry);
            objOut.flush();
            objOut.close();
            bytes = bytesOut.toByteArray();
            bytesOut.flush();
            bytesOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        newRow.put(ImageDbOpener.COL_IMAGEENTRY_OBJECT, bytes);
        imageDB.insert(ImageDbOpener.TABLE_NAME, null, newRow);

    }

    private DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener listen) {

        Calendar cal = Calendar.getInstance();
        return new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {

            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            Calendar earliest = Calendar.getInstance();
            earliest.set(1995, 06, 20);
            if (cal.after(Calendar.getInstance())) {

                alertDate("future");

            } else if (cal.before(earliest)) {

                alertDate("past");
            } else {

                DateFormat imageDate = new SimpleDateFormat("yyyy-MM-dd");
                String newDate = imageDate.format(cal.getTime());
                ImageQuery request = new ImageQuery();
                request.execute("https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=" + newDate);
            }
        }
    }

    private class ImageQuery extends AsyncTask<String, Integer, String> {

        String title;
        String date;
        String url;
        String hdUrl;
        String explanation;
        String mediaType;
        Bitmap theImage;

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
                getImageData(getImageInfo(args[0]));
                publishProgress(100);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            ProgressBar imageProgress = findViewById(R.id.downloadProgress);
            imageProgress.setVisibility(View.VISIBLE);
            imageProgress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            ProgressBar imageProgress = findViewById(R.id.downloadProgress);

            if(this.mediaType == null || !this.mediaType.contentEquals("image")) {

                alertDate("video");
                imageProgress.setVisibility(View.INVISIBLE);
                imageProgress.setProgress(0);
                return;
            }

            TextView imageTitle = findViewById(R.id.searchImageName);
            TextView imageDate = findViewById(R.id.searchImageDate);
            TextView imageUrl = findViewById(R.id.searchImageURL);
            TextView imageHDUrl = findViewById(R.id.searchImageHdURL);
            TextView imageExplanation = findViewById(R.id.searchImageExplanation);
            ImageView newImage = findViewById(R.id.searchImageView);
            TextView searchTitle = findViewById(R.id.searchImageTitle);

            imageTitle.setText(this.title);
            imageHDUrl.setText(this.hdUrl);
            Button save = findViewById(R.id.saveImageButton);
            save.setVisibility(View.VISIBLE);
            searchTitle.setText(R.string.searchImageResult);
            imageDate.setText(this.date);
            imageUrl.setText(this.url);
            imageExplanation.setText(this.explanation);
            newImage.setImageBitmap(this.theImage);
            imageProgress.setVisibility(View.INVISIBLE);
            super.onPostExecute(s);
        }

        private void getImageData(String imageURL) throws IOException {

            Bitmap image = null;
            HttpURLConnection urlConnection = getConnection(this.url);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {

                image = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }

            this.theImage = image;
        }

        private String getImageInfo(String imageURL) throws IOException, JSONException {

            HttpURLConnection urlConnection = getConnection(imageURL);

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
                JSONObject jObject = new JSONObject(result);
                this.explanation = jObject.getString("explanation");
                this.date = jObject.getString("date");
                publishProgress(20);
                this.title = jObject.getString("title");
                publishProgress(40);
                this.url = jObject.getString("url");
                publishProgress(60);
                this.hdUrl = jObject.getString("hdurl");
                publishProgress(80);
                this.mediaType = jObject.getString("media_type");
                System.out.println(this.mediaType);
            }
            return this.url;
        }
    }
}