package com.owen.cst2355finalproject;

public class Constants {

    public static final String VERSION_NAME = "Version " + BuildConfig.VERSION_NAME;

    public static final String PASSWORD_STRING = "password";
    public static final String LAST_LOGIN_STRING = "lastLogin";
    public static final String INCORRECT_PASSWORD_STRING = "Your password is incorrect.  Please try again!";
    public static final String LOGIN_EMAIL_STRING= "loginEmail";
    public static final String LOGIN_PASS_STRING ="loginPass";

    public static final String LOGIN_SHARED_PREF_NAME ="login";
    public static final String PASS_FILE_SHARED_PREF_NAME = "passFile";

    public static final String DELETE_DIALOG_TEXT = "Do you want to delete this?";
    public static final String DELETE_IMAGE_INFO = "The selected image is: %s\nThe image date is: %s";

    public static final String NASA_FETCH_URL = "https://api.nasa.gov/planetary/apod?api_key=%s&date=%s&thumbs=true";
    public static final String API_KEY = "apiKey";
    public static final String KEY = "key";
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    // database constants
    public static final String TABLE_NAME_IMAGE_ENTRY = "ImageEntry";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_EXPLANATION = "explanation";
    public static final String COL_MEDIA_TYPE = "mediaType";
    public static final String COL_URL = "url";
    public static final String COL_HD_URL = "hdUrl";
    public static final String COL_THUMBNAIL_URL = "thumbnailUrl";
    public static final String COL_COPYRIGHT = "copyright";
    public static final String COL_IMAGE_FILE = "imageFile";
}
