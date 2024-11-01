package com.owen.cst2355finalproject;

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

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.owen.cst2355finalproject.deserializers.MediaTypeDeserializer;
import com.owen.cst2355finalproject.entities.ImageEntryEntity;
import com.owen.cst2355finalproject.enums.MediaType;
import com.owen.cst2355finalproject.pojos.ImageEntry;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class which is for searching the new images.  Contains a DatePickerDialog and Async
 * functionality.
 */

public class SearchImage extends MainToolBar {
    private static final ExecutorService FETCH_IMAGE_THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final Object executorServiceLock = new Object();
    private static final Date EARLIEST_DATE = new Date(new Calendar
            .Builder()
            .setDate(1995, Calendar.JUNE, 20)
            .build().getTimeInMillis());
    private DatePickerDialog datePicker;
    private ApplicationDAO dao;
    private ImageEntry newImage;
    private Gson gson;

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
    }

    @Override
    public void initialize() {
        super.initialize();
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
        searchDate.setOnClickListener((click) -> datePicker.show());
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
        final MediaTypeDeserializer deserializer = new MediaTypeDeserializer();
        gson = new GsonBuilder()
                .registerTypeAdapter(MediaType.class, deserializer)
                .create();
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
     * Adds the new ImageEntry object to the database.  First it gets the information
     * from the fields, then it checks to confirm if the image is already saved in which case it exits
     * the method.  After this, it gets the next row ID from the database to create the object with.
     * It creates the ImageEntryObject then serializes it to a byte array, and puts it in the database
     * as a blob, and it adds the entry to the in memory ArrayList for current viewing.
     *
     */

    private void addToDB() {
        final ImageEntryEntity entity = createImageEntryEntity();
        final long id = dao.createEntry(entity);
        newImage.setId(id);
        ImageInfoWrapper.addImage(newImage);
        Toast.makeText(this, getString(R.string.saveToast), Toast.LENGTH_LONG).show();
    }

    private ImageEntryEntity createImageEntryEntity() {
        final ImageEntryEntity entity = new ImageEntryEntity();
        entity.setCopyright(newImage.getCopyright());
        entity.setExplanation(newImage.getExplanation());
        entity.setUrl(newImage.getUrl());
        entity.setHdURL(newImage.getHdURL());
        entity.setMediaType(newImage.getMediaType());
        entity.setDate(newImage.getDate());
        entity.setTitle(newImage.getTitle());
        entity.setImageFile(newImage.getImageFile());
        entity.setThumbnailUrl(newImage.getThumbnailUrl());
        return entity;
    }

    /**
     * Returns a new DatePickerDialog
     * @param listen the Listener object
     * @return new DatePickerDialog
     */
    private DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener listen) {
        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(this, listen, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(EARLIEST_DATE.getTime());
        datePicker.getDatePicker().setMaxDate(cal.getTimeInMillis());
        return datePicker;
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
        final TextView imageCopyright = findViewById(R.id.searchImageCopyright);
        final ImageView newImage = findViewById(R.id.searchImageView);

        imageTitle.setText(entry.getTitle());
        imageDate.setText(entry.getDate());
        imageUrl.setText(entry.getUrl());
        if (StringUtils.isNotEmpty(entry.getHdURL())) {
            imageHDUrl.setText(entry.getHdURL());
        }
        imageExplanation.setText(entry.getExplanation());
        newImage.setImageBitmap(entry.getImageFileAsBitMap());

        final String copyright = entry.getCopyright() != null
                ? StringUtils.trim(entry.getCopyright()) : getString(R.string.notAvailable);
        imageCopyright.setText(copyright);

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
            final Date pickedDate = new Date(
                    new Calendar
                            .Builder()
                            .setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth())
                            .build()
                            .getTimeInMillis());
            final DateFormat imageDate = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
            final String newDate = imageDate.format(pickedDate.getTime());
            getNewImageEntry(newDate);
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
                newImage = getImageInfo(this.url);
                switch (newImage.getMediaType()) {
                    case IMAGE:
                        newImage.setImageFile(getImageData(newImage.getUrl()));
                        break;
                    case VIDEO:
                        newImage.setImageFile(getImageData(newImage.getThumbnailUrl()));
                        break;
                }
                runOnUiThread(()->{
                    try {
                        setScreen(newImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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

        private ImageEntry getImageInfo(String imageURL) throws IOException, JSONException {
            final HttpURLConnection urlConnection = getConnection(imageURL);
            if (urlConnection.getResponseCode() == 200) {
                final InputStream inputStream = new URL(imageURL).openStream();
                try(final BufferedReader reader =
                            new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8)) {
                    return gson.fromJson(reader, ImageEntry.class);
                } finally {
                    inputStream.close();
                    urlConnection.disconnect();
                }
            }
            return new ImageEntry();
        }
    }
}