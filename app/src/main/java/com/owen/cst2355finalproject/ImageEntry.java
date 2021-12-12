package com.owen.cst2355finalproject;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

public class ImageEntry implements Serializable {

    private long id;
    private final String title;
    private final URL url;
    private final String date;
    private final URL hdURL;
    private final Bitmap imageFile;

    public ImageEntry(long id, String title, URL url, String date, URL hdURL, Bitmap imageFile) {

        this.id = id;
        this.url = url;
        this.date = date;
        this.hdURL = hdURL;
        this.title = title;
        this.imageFile = imageFile;

    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public URL getHdURL() {
        return hdURL;
    }

    public Bitmap getImageFile() {
        return imageFile;
    }
}
