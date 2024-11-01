package com.owen.cst2355finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.owen.cst2355finalproject.entities.ImageEntryEntity;

public class ApplicationDAO {

    private static boolean hasBeenLoaded = false;
    private final ImageDbOpener dbOpener;

    public ApplicationDAO(Context context) {
        dbOpener = new ImageDbOpener(context);
        if (ImageInfoWrapper.listSize() == 0 && !hasBeenLoaded) {
            loadFromDB();
            hasBeenLoaded = true;
        }
    }

    public SQLiteDatabase getImageDb(boolean isWriteable) {
        return isWriteable ? dbOpener.getWritableDatabase() : dbOpener.getReadableDatabase();
    }

    public long createEntry(final ImageEntryEntity imageEntry) {
        final ContentValues newRow = new ContentValues();
        final SQLiteDatabase database = getImageDb(true);
        long newId = 0;
        try {
            newRow.put(Constants.COL_TITLE, imageEntry.getTitle());
            newRow.put(Constants.COL_DATE, imageEntry.getDate());
            newRow.put(Constants.COL_EXPLANATION, imageEntry.getExplanation());
            newRow.put(Constants.COL_MEDIA_TYPE, imageEntry.getMediaType().toString());
            newRow.put(Constants.COL_URL, imageEntry.getUrl());
            newRow.put(Constants.COL_HD_URL, imageEntry.getHdURL());
            newRow.put(Constants.COL_COPYRIGHT, imageEntry.getCopyright());
            newRow.put(Constants.COL_IMAGE_FILE, imageEntry.getImageFile());
            newRow.put(Constants.COL_THUMBNAIL_URL, imageEntry.getThumbnailUrl());
            database.beginTransactionNonExclusive();
            newId = database.insert(Constants.TABLE_NAME_IMAGE_ENTRY, null, newRow);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            database.endTransaction();
            database.close();
        }
        return newId;
    }

    public void deleteEntry(long id) {
        final SQLiteDatabase database = getImageDb(true);
        try {
            database.beginTransactionNonExclusive();
            database.delete(
                    Constants.TABLE_NAME_IMAGE_ENTRY,
                    Constants.COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while deleting Entry");
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    /**
     * Gets the results of the query in the Image table of the database.  Then calls the convertFromBlob() method
     * to convert the Blob back into an ImageEntry object, and the ImageEntry object is put
     * into the CopyOnWriteArrayList.  This is done for each row in the Cursor.
     */
    private void loadFromDB()  {
        new LoadingThread(getImageDb(false)).start();
    }
}
