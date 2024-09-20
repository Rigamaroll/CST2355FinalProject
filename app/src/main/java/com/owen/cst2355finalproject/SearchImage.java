package com.owen.cst2355finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private static final ExecutorService FETCH_IMAGE_THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final Object executorServiceLock = new Object();
    private DatePickerDialog datePicker;
    private ApplicationDAO dao;
    private ImageEntry newImage;

    /**
     * creation method for the activity which sets the ImageInfoWrapper containing
     * the necessary image info the app.  It then sets up the Toolbar, the DatePicker, and lets
     * clicking on the HdURL go to the browser to see the HD image.
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);
        dao = new ApplicationDAO(this);
        initialize();
        getToolbar().setTitle(getString(R.string.searchImageTitle));

        final Button searchDate = findViewById(R.id.searchImageButton);
        final Button saveImage = findViewById(R.id.saveImageButton);

        final TextView hdURL = findViewById(R.id.searchImageHdURL);
        hdURL.setOnClickListener((click) -> {
            final Uri imageLocation = Uri.parse(hdURL.getText().toString());
            final Intent goBrowser = new Intent(Intent.ACTION_VIEW, imageLocation);
            startActivity(goBrowser);
        });

        datePicker = getDatePickerDialog(new DatePickerListener());
        datePicker.setTitle(getString(R.string.pickerTitle));
        searchDate.setOnClickListener((click) -> {
            datePicker.show();
        });

        saveImage.setOnClickListener((click) -> {
            final TextView imageTitle = findViewById(R.id.searchImageName);
            if (ImageInfoWrapper.exists(newImage.getDate())) {
                Snackbar.make(imageTitle, getString(R.string.haveImage), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.teal_700))
                        .show();
            } else {
                addToDB();
            }
        });
    }

    public static void shutdownExecutorService() {

        synchronized (executorServiceLock) {
            FETCH_IMAGE_THREAD_POOL.shutdown();
        }
    }

    private void progressVis(ProgressBar progress, boolean isVisible) {
        progress.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Creates an alert dialog if the date picked is not a valid date.
     * @param reason reason for this dialog being created
     */

    protected void alertDate(BadDateReason reason) {
        String alertString = null;
        switch (reason) {
            case PAST:
                alertString = getString(R.string.dateRangePast);
                break;
            case FUTURE:
                alertString = getString(R.string.dateRangeFuture);
                break;
            case VIDEO:
                alertString = getString(R.string.dateRangeVideo);
                break;
            default:
                break;
        }
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        progressVis(findViewById(R.id.progressId), false);
        alertDialogBuilder.setTitle(getString(R.string.dateOutRange))
                .setMessage(alertString)
                .setPositiveButton(getString(R.string.yes), (click, arg) -> {
                    datePicker.show();
                })
                .setNegativeButton(getString(R.string.no), (click, arg) -> {
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
        Toast.makeText(this, getString(R.string.saveToast), Toast.LENGTH_LONG).show();
    }

    /**
     * Returns a new DatePickerDialog
     * @param listen the Listener object
     * @return new DatePickerDialog
     */
    private DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener listen) {
        final Calendar cal = Calendar.getInstance();
        return new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private void getNewImageEntry(String newDate) {
        synchronized (executorServiceLock) {
            FETCH_IMAGE_THREAD_POOL.execute(new FetchPhotoThread(
                    String.format(Constants.NASA_FETCH_URL,
                            getSharedPreferences(Constants.API_KEY, MODE_PRIVATE).getString(Constants.KEY, ""),
                            newDate)));
        }
        progressVis(findViewById(R.id.progressId), true);
    }

    private void setScreen(ImageEntry entry) throws IOException {

        final ProgressBar progress = findViewById(R.id.progressId);
        progress.setVisibility(View.INVISIBLE);

        final TextView imageTitle = findViewById(R.id.searchImageName);
        final TextView imageDate = findViewById(R.id.searchImageDate);
        final TextView imageUrl = findViewById(R.id.searchImageURL);
        final TextView imageHDUrl = findViewById(R.id.searchImageHdURL);
        final TextView imageExplanation = findViewById(R.id.searchImageExplanation);
        final ImageView newImage = findViewById(R.id.searchImageView);

        imageTitle.setText(entry.getTitle());
        imageDate.setText(entry.getDate());
        imageUrl.setText(entry.getUrl());
        imageHDUrl.setText(entry.getHdURL());
        imageExplanation.setText(entry.getExplanation());
        newImage.setImageBitmap(entry.getImageFile());

        final Button save = findViewById(R.id.saveImageButton);
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
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            final Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            final Calendar earliest = Calendar.getInstance();
            earliest.set(1995, Calendar.JUNE, 20);
            if (cal.after(Calendar.getInstance())) {
                alertDate(BadDateReason.FUTURE);
            } else if (cal.before(earliest)) {
                alertDate(BadDateReason.PAST);
            } else {
                final DateFormat imageDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
                final String newDate = imageDate.format(cal.getTime());
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
                final JSONObject jObject = getImageInfo(this.url);
                final String mediaType = jObject.getString(Constants.MEDIA_TYPE_STRING);

                if (mediaType == null || !mediaType.contentEquals(Constants.IMAGE_STRING)) {
                    runOnUiThread(()-> alertDate(BadDateReason.VIDEO));
                } else {
                    final String explanation = jObject.getString(Constants.EXPLANATION_STRING);
                    final String date = jObject.getString(Constants.DATE_STRING);
                    final String title = jObject.getString(Constants.TITLE_STRING);
                    final String url = jObject.getString(Constants.URL_STRING);
                    final String hdUrl = jObject.getString(Constants.HD_URL_STRING);
                    final Bitmap image = getImageData(url);

                    runOnUiThread(()->{
                        try {
                            newImage = new ImageEntry(dao.getNextKeyNumber(), title, url, date, hdUrl, explanation, image);
                            setScreen(newImage);
                        } catch (IOException e) {
                            e.printStackTrace();
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
            return (HttpURLConnection) new URL(location).openConnection();
        }

        /**
         * Downloads the image from the URL provided.
         *
         * @param imageURL the location of the image
         * @throws IOException
         */
        private Bitmap getImageData(String imageURL) throws IOException {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = getConnection(imageURL);
                return urlConnection.getResponseCode() == 200 ?
                        BitmapFactory.decodeStream(urlConnection.getInputStream()) : null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
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
            final HttpURLConnection urlConnection = getConnection(imageURL);
            JSONObject jObject = null;
            if (urlConnection.getResponseCode() == 200) {
                final InputStream inputStream = new URL(imageURL).openStream();
                try(final BufferedReader reader =
                            new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8)) {
                    final StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    final String result = sb.toString();
                    jObject = new JSONObject(result);
                } finally {
                    inputStream.close();
                    urlConnection.disconnect();
                }
            }
            return jObject;
        }
    }
}