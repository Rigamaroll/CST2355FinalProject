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
        try (final SQLiteDatabase database = getImageDb(true)) {
            newRow.put(Constants.COL_TITLE, imageEntry.getTitle());
            newRow.put(Constants.COL_DATE, imageEntry.getDate());
            newRow.put(Constants.COL_EXPLANATION, imageEntry.getExplanation());
            newRow.put(Constants.COL_MEDIA_TYPE, imageEntry.getMediaType().toString());
            newRow.put(Constants.COL_URL, imageEntry.getUrl());
            newRow.put(Constants.COL_HD_URL, imageEntry.getHdURL());
            newRow.put(Constants.COL_COPYRIGHT, imageEntry.getCopyright());
            newRow.put(Constants.COL_IMAGE_FILE, imageEntry.getImageFile());
            newRow.put(Constants.COL_THUMBNAIL_URL, imageEntry.getThumbnailUrl());
            return database.insert(Constants.TABLE_NAME_IMAGE_ENTRY, null, newRow);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteEntry(long id) {
        try (final SQLiteDatabase database = getImageDb(true)) {
            database.delete(
                    Constants.TABLE_NAME_IMAGE_ENTRY,
                    Constants.COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
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
