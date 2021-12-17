package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Class for accessing the Database.
 */

public class ImageDbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ImageDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "Image";
    public final static String COL_IMAGEENTRY_OBJECT = "ImageEntry";
    public final static String COL_ID = "_id";

    public ImageDbOpener(@Nullable Context context) {

        super(context, DATABASE_NAME, null, VERSION_NUM);

    }

    /**
     * Creates the database if it doesn't already exist.
     *
     * @param imageDB
     */

    @Override
    public void onCreate(SQLiteDatabase imageDB) {

        imageDB.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_IMAGEENTRY_OBJECT + " BLOB);");
    }

    /**
     * if the version number gets upgraded this gets called.
     *
     * @param imageDB
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase imageDB, int oldVersion, int newVersion) {

        onCreate(imageDB);

    }
}
