package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageDbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ImageDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "Image";
    public final static String COL_IMAGE = "Image";
    public final static String COL_DATE = "Date";
    public final static String COL_URL = "URL";
    public final static String COL_HD_URL = "HDUrl";
    public final static String COL_ID = "_id";


    /*
    Constructor for the Database Opener Class
     */
    public ImageDbOpener(@Nullable Context context) {

        super(context, DATABASE_NAME, null, VERSION_NUM);

    }

    /*
    onCreate Created the database if it doesn't already exist

     */
    @Override
    public void onCreate(SQLiteDatabase imageDB) {

        imageDB.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " TEXT,"
                + COL_URL + " TEXT,"
                + COL_HD_URL + " TEXT,"
                + COL_IMAGE + " BLOB);");

    }

    /*
    if the version number gets upgrade this gets called.

     */
    @Override
    public void onUpgrade(SQLiteDatabase imageDB, int oldVersion, int newVersion) {

        onCreate(imageDB);

    }
}
