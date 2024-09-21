package com.owen.cst2355finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadingThread extends Thread {

    private static final String GET_ALL_RESULTS_QUERY =
            String.format("SELECT %s,%s FROM IMAGE",Constants.COL_ID, Constants.COL_IMAGE_ENTRY_OBJECT);

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
                final Cursor results = database.rawQuery(GET_ALL_RESULTS_QUERY, null)){

            //find the column indices:
            final int imageObject = results.getColumnIndex(Constants.COL_IMAGE_ENTRY_OBJECT);

            //iterate over the results, return true if there is a next item:
            while (results.moveToNext()) {
                final byte[] imageEntryObject = results.getBlob(imageObject);
                final ImageEntry newImageEntry = convertFromBlob(imageEntryObject);
                ImageInfoWrapper.addImage(newImageEntry);
            }
        } finally {
            database.close();
        }
    }

    /**
     * Converts the Blob back into an ImageEntry object
     *
     * @param imageEntryObject the byte[] object that was a blob.
     * @return the ImageEntry object
     */
    private ImageEntry convertFromBlob(byte[] imageEntryObject) {
        ImageEntry newImageEntry = null;
        try (final ByteArrayInputStream imageInput = new ByteArrayInputStream(imageEntryObject);
             final ObjectInputStream newImage = new ObjectInputStream(imageInput)){
            newImageEntry = (ImageEntry) newImage.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newImageEntry;
    }

}
