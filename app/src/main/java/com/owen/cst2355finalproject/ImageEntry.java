package com.owen.cst2355finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Container object for all the Image information.  Implements
 * Serializable so it can be passed in and out of the database with one step,
 * and it doesn't need to be rebuilt everytime.
 * Needed to store the image as a byte array so that the object
 * could be serialized into the database.  The bitmap isn't a serializable
 * object.
 */

public class ImageEntry implements Serializable {

    private final long id;
    private final String title;
    private final String url;
    private final String date;
    private final String hdURL;
    private final String explanation;
    private byte[] imageFile = null;

    public ImageEntry(long id, String title, String url, String date, String hdURL, String explanation, Bitmap imageFile) {

        this.id = id;
        this.url = url;
        this.date = date;
        this.hdURL = hdURL;
        this.explanation = explanation;
        this.title = title;
        try {
            setImageFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getHdURL() {
        return hdURL;
    }

    public String getExplanation() {
        return explanation;
    }

    /**
     * Returns the Bitmap image being stored as a byte[] by converting it back into a Bitmap
     *
     * @return The Bitmap image
     * @throws IOException
     */

    public Bitmap getImageFile() throws IOException {

        Bitmap imageBits;
        ByteArrayInputStream imageInput = new ByteArrayInputStream(this.imageFile);
        ObjectInputStream newImage = new ObjectInputStream(imageInput);
        imageBits = BitmapFactory.decodeStream(newImage);
        newImage.close();
        imageInput.close();

        return imageBits;
    }

    /**
     * Sets the imageFile byte array with the Bitmap image.
     *
     * @param serialize the Bitmap image for the object
     * @throws IOException
     */
    public void setImageFile(Bitmap serialize) throws IOException {

        byte[] converted;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(bytes);
        serialize.compress(Bitmap.CompressFormat.JPEG, 70, objOut);
        converted = bytes.toByteArray();

        objOut.flush();
        objOut.close();
        bytes.flush();
        bytes.close();
        this.imageFile = converted;
    }

}
