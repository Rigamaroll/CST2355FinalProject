package com.owen.cst2355finalproject.entities;

import com.owen.cst2355finalproject.enums.MediaType;

public class ImageEntryEntity {

    private long id;
    private String title;
    private String date;
    private String explanation;
    private MediaType mediaType;
    private String url;
    private String hdURL;
    private String thumbnailUrl;
    private String copyright;
    private byte[] imageFile;

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

    public byte[] getImageFile() { return imageFile; }

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
}
