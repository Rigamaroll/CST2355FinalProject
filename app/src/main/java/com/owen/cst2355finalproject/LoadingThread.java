package com.owen.cst2355finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.owen.cst2355finalproject.enums.MediaType;
import com.owen.cst2355finalproject.pojos.ImageEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadingThread extends Thread {

    private final SQLiteDatabase database;

    public LoadingThread(SQLiteDatabase db) {
        database = db;
    }

    @Override
    public void run() {
        loadFromDB();
    }

    /**
     * Gets the results of the query in the Image table of the database.  Then calls the convertFromBlob() method
     * to convert the Blob back into an ImageEntry object, and the ImageEntry object is put
     * into the CopyOnWriteArrayList.  This is done for each row in the Cursor.
     */
    private void loadFromDB() {
        try (//query all the results from the database:
                final Cursor results = database.rawQuery(getLoadSql(), null)){

            //iterate over the results, return true if there is a next item:
            while (results.moveToNext()) {
                final ImageEntry entryPojo = new ImageEntry();
                entryPojo.setImageFile(results.getBlob(results.getColumnIndex(Constants.COL_IMAGE_FILE)));
                entryPojo.setTitle(results.getString(results.getColumnIndex(Constants.COL_TITLE)));
                entryPojo.setDate(results.getString(results.getColumnIndex(Constants.COL_DATE)));
                entryPojo.setExplanation(results.getString(results.getColumnIndex(Constants.COL_EXPLANATION)));
                entryPojo.setHdURL(results.getString(results.getColumnIndex(Constants.COL_HD_URL)));
                entryPojo.setUrl(results.getString(results.getColumnIndex(Constants.COL_URL)));
                entryPojo.setMediaType(MediaType.valueOf(results.getString(results.getColumnIndex(Constants.COL_MEDIA_TYPE))));
                entryPojo.setId(results.getLong(results.getColumnIndex(Constants.COL_ID)));
                entryPojo.setCopyright(results.getString(results.getColumnIndex(Constants.COL_COPYRIGHT)));
                ImageInfoWrapper.addImage(entryPojo);
            }
        } finally {
            database.close();
        }
    }

    private String getLoadSql() {
            return String.format("SELECT %s,%s,%s,%s,%s,%s,%s,%s,%s FROM %s",
                    Constants.COL_ID,
                    Constants.COL_TITLE,
                    Constants.COL_DATE,
                    Constants.COL_EXPLANATION,
                    Constants.COL_MEDIA_TYPE,
                    Constants.COL_URL,
                    Constants.COL_HD_URL,
                    Constants.COL_COPYRIGHT,
                    Constants.COL_IMAGE_FILE,
                    Constants.TABLE_NAME_IMAGE_ENTRY);
    }
}
