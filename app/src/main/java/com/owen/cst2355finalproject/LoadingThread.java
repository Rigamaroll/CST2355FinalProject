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
                final Cursor results = database.rawQuery(getLoadSql(ImageDbOpener.getVersionNum()), null)){

            //find the column indices:
            final int imageObject = results.getColumnIndex(Constants.COL_IMAGE_ENTRY_OBJECT);

            //iterate over the results, return true if there is a next item:
            while (results.moveToNext()) {
                final byte[] imageEntryObject = results.getBlob(imageObject);
                final ImageEntry entryPojo;
                if (ImageDbOpener.getVersionNum() <= 2) {
                    final com.owen.cst2355finalproject.ImageEntry entry = convertFromBlobOld(imageEntryObject);
                    entryPojo = new ImageEntry();
                    final Bitmap image = entry.getImageFile();
                    entryPojo.setImageFile(image);
                    entryPojo.setTitle(entry.getTitle());
                    entryPojo.setDate(entry.getDate());
                    entryPojo.setExplanation(entry.getExplanation());
                    entryPojo.setHdURL(entry.getHdURL());
                    entryPojo.setUrl(entry.getUrl());
                    entryPojo.setMediaType(MediaType.image);
                    entryPojo.setId(entry.getId());
                } else {
                    entryPojo = convertFromBlob(imageEntryObject);
                }
                ImageInfoWrapper.addImage(entryPojo);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private com.owen.cst2355finalproject.ImageEntry convertFromBlobOld(byte[] imageEntryObject) {
        com.owen.cst2355finalproject.ImageEntry newImageEntry = null;
        try (final ByteArrayInputStream imageInput = new ByteArrayInputStream(imageEntryObject);
             final ObjectInputStream newImage = new ObjectInputStream(imageInput)){
            newImageEntry = (com.owen.cst2355finalproject.ImageEntry) newImage.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newImageEntry;
    }

    private String getLoadSql(final int dbVersion) {
        if (dbVersion < 3) {
            return String.format("SELECT %s,%s FROM IMAGE",Constants.COL_ID, Constants.COL_IMAGE_ENTRY_OBJECT);
        } else {
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
}
