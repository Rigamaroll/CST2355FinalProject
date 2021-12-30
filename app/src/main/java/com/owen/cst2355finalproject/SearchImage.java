package com.owen.cst2355finalproject;

import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class which is for searching the new images.  Contains a DatePickerDialog and Async
 * functionality.
 */

public class SearchImage extends MainToolBar {

    DatePickerDialog datePicker;
    ApplicationDAO dao;
    ImageEntry newImage;
    ExecutorService getImageThreadPool;

    /**
     * creation method for the activity which sets the ImageInfoWrapper containing
     * the necessary image info the app.  It then sets up the Toolbar, the DatePicker, and lets
     * clicking on the HdURL go to the browser to see the HD image.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);
        dao = new ApplicationDAO(this);
        initialize();
        getToolbar().setTitle(R.string.searchImageTitle);
        getImageThreadPool = Executors.newSingleThreadExecutor();

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
            TextView imageTitle = findViewById(R.id.searchImageName);
            if (ImageInfoWrapper.exists(newImage.getDate())) {

                Snackbar.make(imageTitle, getString(R.string.haveImage), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.teal_700))
                        .show();

            } else {

                addToDB();
            }
        });
    }

    /**
     * Creates an alert dialog if the date picked is not a valid date.
     * @param reason reason for this dialog being created
     */

    protected void alertDate(String reason) {
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
     */

    private void addToDB() {

        dao.createEntry(newImage);
        ImageInfoWrapper.setImages(newImage);
        Toast saveToast = Toast.makeText(this, R.string.saveToast, Toast.LENGTH_LONG);
        saveToast.show();
    }

    /**
     * Returns a new DatePickerDialog
     * @param listen the Listener object
     * @return new DatePickerDialog
     */
    private DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener listen) {

        Calendar cal = Calendar.getInstance();
        return new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private void getNewImageEntry(String newDate) {

        getImageThreadPool.execute(new FetchPhotoThread("https://api.nasa.gov/planetary/apod?api_key="
                + getSharedPreferences("apiKey", MODE_PRIVATE).getString("key", "")
                + "&date=" + newDate));
    }

    private void setScreen(ImageEntry entry) throws IOException {

        TextView imageTitle = findViewById(R.id.searchImageName);
        TextView imageDate = findViewById(R.id.searchImageDate);
        TextView imageUrl = findViewById(R.id.searchImageURL);
        TextView imageHDUrl = findViewById(R.id.searchImageHdURL);
        TextView imageExplanation = findViewById(R.id.searchImageExplanation);
        ImageView newImage = findViewById(R.id.searchImageView);

        imageTitle.setText(entry.getTitle());
        imageDate.setText(entry.getDate());
        imageUrl.setText(entry.getUrl());
        imageHDUrl.setText(entry.getHdURL());
        imageExplanation.setText(entry.getExplanation());
        newImage.setImageBitmap(entry.getImageFile());
        Button save = findViewById(R.id.saveImageButton);
        save.setVisibility(View.VISIBLE);
    }

    /**
     * Listener for the DatePicker
     */
    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        /**
         * Sets what happens when a date is chosen in the date dialog.
         *
         * @param datePicker DatePicker Object
         * @param year
         * @param month
         * @param day
         */
        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {

            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            Calendar earliest = Calendar.getInstance();
            earliest.set(1995, Calendar.JUNE, 20);
            if (cal.after(Calendar.getInstance())) {

                alertDate("future");

            } else if (cal.before(earliest)) {

                alertDate("past");
            } else {

                DateFormat imageDate = new SimpleDateFormat("yyyy-MM-dd");
                String newDate = imageDate.format(cal.getTime());
                getNewImageEntry(newDate);
            }
        }
    }

    private class FetchPhotoThread implements Runnable {

        String url;

        public FetchPhotoThread(String url) {

            this.url = url;

        }

        /**
         * calls each appropriate method to get the image information for displaying
         */
        @Override
        public void run() {

            try {

                JSONObject jObject = getImageInfo(this.url);
                String mediaType = jObject.getString("media_type");

                if (mediaType == null || !mediaType.contentEquals("image")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDate("video");
                        }
                    });

                } else {

                    String explanation = jObject.getString("explanation");
                    String date = jObject.getString("date");
                    String title = jObject.getString("title");
                    String url = jObject.getString("url");
                    String hdUrl = jObject.getString("hdurl");
                    Bitmap image = getImageData(url);

                    newImage = new ImageEntry(dao.getNextKeyNumber(), title, url, date, hdUrl, explanation, image);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setScreen(newImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (IOException | JSONException e) {

                e.printStackTrace();
            }
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
         *
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
         *
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
}