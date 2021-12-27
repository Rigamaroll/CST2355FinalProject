package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Class containing the list in memory for the ListView and image information, a DbOpener object
 * for getting the database.  Takes the Context of the opening activity when constructed.
 * The CopyOnWriteArrayList is static and thread safe so that all the activities can access the same one.
 */

public class ImageInfoWrapper {

    private static CopyOnWriteArrayList<ImageEntry> images;
    private ImageDbOpener opener;

    public ImageInfoWrapper(Context context) {

        images = new CopyOnWriteArrayList<ImageEntry>();
        opener = new ImageDbOpener(context);
        if (images.size() == 0) {

           loadFromDB();

        }
    }

    public ImageEntry getImages(int position) {
        return images.get(position);
    }

    public void setImages(ImageEntry image) {
        images.add(image);
    }

    public void deleteImages(int position) {

        images.remove(position);
    }

    public int listSize() {

        return images.size();
    }

    /**
     * Returns a database either writable or readable
     *
     * @param type true or false for Writable or Readable respectively
     * @return the SQLite database either readable or writable
     */

    public SQLiteDatabase getImageDb(boolean type) {

        SQLiteDatabase imageDb = type ? opener.getWritableDatabase() : opener.getReadableDatabase();
        return imageDb;
    }

    /**
     * Checks if the CopyOnWriteArrayList contains the existing image by checking the dates.
     *
     * @param imageDate Date of the image to be checked.
     * @return true if the image is already in the database.
     */

    public boolean exists(String imageDate) {

        for (ImageEntry dates : images) {

            if (dates.getDate().contentEquals(imageDate)) {

                return true;
            }
        }
        return false;
    }

    /**
     * Gets the results of the query in the Image table of the database.  Then calls the convertFromBlob() method
     * to convert the Blob back into an ImageEntry object, and the ImageEntry object is put
     * into the CopyOnWriteArrayList.  This is done for each row in the Cursor.
     */

    private void loadFromDB() {

        //query all the results from the database:
        Cursor results = getImageDb(true).rawQuery("SELECT " + ImageDbOpener.COL_ID + ", "
                + ImageDbOpener.COL_IMAGEENTRY_OBJECT + " FROM IMAGE;", null);

        //find the column indices:

        int idIndex = results.getColumnIndex(ImageDbOpener.COL_ID);
        int imageObject = results.getColumnIndex(ImageDbOpener.COL_IMAGEENTRY_OBJECT);

        //iterate over the results, return true if there is a next item:

        while (results.moveToNext()) {

            byte[] imageEntryObject = results.getBlob(imageObject);
            ImageEntry newImageEntry = convertFromBlob(imageEntryObject);
            setImages(newImageEntry);
        }
        results.close();
        getImageDb(true).close();
    }

    /**
     * Converts the Blob back into an ImageEntry object
     *
     * @param imageEntryObject the byte[] object that was a blob.
     * @return the ImageEntry object
     */
    public ImageEntry convertFromBlob(byte[] imageEntryObject) {
        ImageEntry newImageEntry = null;
        try {
            ByteArrayInputStream imageInput = new ByteArrayInputStream(imageEntryObject);
            ObjectInputStream newImage = new ObjectInputStream(imageInput);
            newImageEntry = (ImageEntry) newImage.readObject();
            newImage.close();
            imageInput.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newImageEntry;
    }
}
