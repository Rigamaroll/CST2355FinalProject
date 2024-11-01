package com.owen.cst2355finalproject.pojos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.annotations.SerializedName;
import com.owen.cst2355finalproject.enums.MediaType;

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
public class ImageEntry implements Serializable{

    private long id;
    private String title;
    private String url;
    private String date;
    @SerializedName("hdurl")
    private String hdURL;
    @SerializedName("thumbnail_url")
    private String thumbnailUrl;
    private String explanation;
    @SerializedName("media_type")
    private MediaType mediaType;
    private String copyright;
    private byte[] imageFile = null;

    public ImageEntry(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHdURL() {
        return hdURL;
    }

    public void setHdURL(String hdURL) {
        this.hdURL = hdURL;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public byte[] getImageFile() { return this.imageFile; }

    public void setImageFile(byte[] imageFile) {
        this.imageFile = imageFile;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Returns the Bitmap image being stored as a byte[] by converting it back into a Bitmap
     *
     * @return The Bitmap image
     * @throws IOException
     */

    public Bitmap getImageFileAsBitMap() throws IOException {
        try ( final ByteArrayInputStream imageInput = new ByteArrayInputStream(this.imageFile);
              final ObjectInputStream newImage = new ObjectInputStream(imageInput)) {
            final Bitmap imageBits = BitmapFactory.decodeStream(newImage);
            return imageBits;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Sets the imageFile byte array with the Bitmap image.
     *
     * @param serialize the Bitmap image for the object
     * @throws IOException
     */
    public void setImageFile(Bitmap serialize) throws IOException {
        try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream objOut = new ObjectOutputStream(bytes)) {
            serialize.compress(Bitmap.CompressFormat.JPEG, 70, objOut);
            final byte[] converted = bytes.toByteArray();
            objOut.flush();
            bytes.flush();
            this.imageFile = converted;
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        return "ImageEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", hdURL='" + hdURL + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", explanation='" + explanation + '\'' +
                ", mediaType=" + mediaType +
                ", copyright='" + copyright + '\''+
                '}';
    }
}
