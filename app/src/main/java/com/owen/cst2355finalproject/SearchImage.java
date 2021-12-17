package com.owen.cst2355finalproject;

import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class which is for searching the new images.  Contains a DatePickerDialog and Async
 * functionality.
 */

public class SearchImage extends MainToolBar {

    DatePickerDialog datePicker;
    ImageInfoWrapper wrap;

    /**
     * creation method for the activitu which sets the ImageInfoWrapper containing
     * the necessary image info the app.  It then sets up the Toolbar, the DatePicker, and lets
     * clicking on the HdURL go to the browser to see the HD image.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);
        wrap = new ImageInfoWrapper(this);
        initialize();
        getToolbar().setTitle(R.string.searchImageTitle);

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

    /**
     * Creates an alert dialog if the date picked is not a valid date.
     * @param reason reason for this dialog being created
     */

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

    /**
     * Adds the new ImageEntry object to the database.  First it gets the information
     * from the fields, then it checks to confirm if the image is already saved in which case it exits
     * the method.  After this, it gets the next row ID from the database to create the object with.
     * It creates the ImageEntryObject then serializes it to a byte array, and puts it in the database
     * as a blob, and it adds the entry to the in memory ArrayList for current viewing.
     *
     * @throws MalformedURLException
     */

    private void addToDB() throws MalformedURLException {

        TextView imageTitle = findViewById(R.id.searchImageName);
        TextView imageDate = findViewById(R.id.searchImageDate);
        TextView imageUrl = findViewById(R.id.searchImageURL);
        TextView imageHDUrl = findViewById(R.id.searchImageHdURL);
        TextView imageExplanation = findViewById(R.id.searchImageExplanation);
        ImageView imageImage = findViewById(R.id.searchImageView);

        String title = String.valueOf(imageTitle.getText());
        String date = String.valueOf(imageDate.getText());

        if (wrap.exists(date)) {

            String snackString = "You already have that date's image";
            Snackbar.make(imageTitle, snackString, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getColor(R.color.teal_700))
                    .show();
            return;
        }

        String url = String.valueOf(imageUrl.getText());
        String hdUrl = String.valueOf(imageHDUrl.getText());
        String explanation = String.valueOf(imageExplanation.getText());
        BitmapDrawable theImage = (BitmapDrawable) imageImage.getDrawable();
        Bitmap newImage = theImage.getBitmap();

        long id = 0;
        ContentValues newRow = new ContentValues();
        Cursor results = wrap.getImageDb(true).rawQuery("SELECT max(seq) FROM sqlite_sequence;", null, null);
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
        wrap.getImageDb(true).insert(ImageDbOpener.TABLE_NAME, null, newRow);
        wrap.setImages(imageEntry);

    }


    /**
     * Returns a new DatePickerDialog
     * @param listen the Listener object
     * @return
     */
    private DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener listen) {

        Calendar cal = Calendar.getInstance();
        return new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Listener for the DatePicker
     */
    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        /**
         * Sets what happens when a date is chosen in the date dialog.
         *
         * @param datePicker
         * @param year
         * @param month
         * @param day
         */
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
                request.execute("https://api.nasa.gov/planetary/apod?api_key="
                        + getSharedPreferences("apiKey", MODE_PRIVATE).getString("key", "")
                        + "&date=" + newDate);
            }
        }
    }

    /**
     * The AsyncTask for querying the new images
     */
    private class ImageQuery extends AsyncTask<String, Integer, String> {

        String title;
        String date;
        String url;
        String hdUrl;
        String explanation;
        String mediaType;
        Bitmap theImage;

        /**
         * gets a new HttpURLConnection
         */
        private HttpURLConnection getConnection(String location) throws IOException {

            URL url = new URL(location);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection;
        }

        /**
         * calls each appropriate method to get the image information for displaying
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

        /**
         * looks after the ProgressBar
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

            ProgressBar imageProgress = findViewById(R.id.downloadProgress);
            imageProgress.setVisibility(View.VISIBLE);
            imageProgress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        /**
         * Determines what happens after the Async is completed.  This will set the ImageView and TextViews
         * for the page, so they can be displayed with the downloaded information.
         * @param s
         */

        @Override
        protected void onPostExecute(String s) {

            ProgressBar imageProgress = findViewById(R.id.downloadProgress);

            if (this.mediaType == null || !this.mediaType.contentEquals("image")) {

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

        /**
         * Downloads the image from the URL provided.
         * @param imageURL the location of the image
         * @throws IOException
         */

        private void getImageData(String imageURL) throws IOException {

            Bitmap image = null;
            HttpURLConnection urlConnection = getConnection(imageURL);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {

                image = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }

            this.theImage = image;
        }

        /**
         * Downloads the JSON object containing the information about the image and its location.
         * It then sets the instance variables with the appropriate JSON fields.
         * @param imageURL location of the JSON object for the image information
         * @return
         * @throws IOException
         * @throws JSONException
         */

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
            }
            return this.url;
        }
    }
}