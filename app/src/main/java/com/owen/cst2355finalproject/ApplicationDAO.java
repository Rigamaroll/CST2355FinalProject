package com.owen.cst2355finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.owen.cst2355finalproject.entities.ImageEntryEntity;
import com.owen.cst2355finalproject.enums.MediaType;
import com.owen.cst2355finalproject.pojos.ImageEntry;

import org.apache.commons.lang3.ThreadUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApplicationDAO {

    private static boolean hasBeenLoaded = false;
    private final ImageDbOpener dbOpener;

    public ApplicationDAO(Context context) {
        dbOpener = new ImageDbOpener(context);
        if (ImageInfoWrapper.listSize() == 0 && !hasBeenLoaded) {
            loadFromDB();
            if (ImageDbOpener.getVersionNum() == 2) {
                migrateV2Db();
            }
            hasBeenLoaded = true;
        }
    }

    public SQLiteDatabase getImageDb(boolean isWriteable) {
        return isWriteable ? dbOpener.getWritableDatabase() : dbOpener.getReadableDatabase();
    }

    public synchronized long getNextKeyNumber() {
        long id = 0;
        try (Cursor results = getImageDb(false)
                .rawQuery("SELECT max(seq) FROM sqlite_sequence;", null, null)) {
            while (results.moveToNext()) {
                id = results.getLong(0) + 1;
            }
            return id;
        }
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
            return database.insert(Constants.TABLE_NAME_IMAGE_ENTRY, null, newRow);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public long createEntryMigration(final ImageEntryEntity imageEntry, SQLiteDatabase database) {
        final ContentValues newRow = new ContentValues();
            newRow.put(Constants.COL_TITLE, imageEntry.getTitle());
            newRow.put(Constants.COL_DATE, imageEntry.getDate());
            newRow.put(Constants.COL_EXPLANATION, imageEntry.getExplanation());
            newRow.put(Constants.COL_MEDIA_TYPE, imageEntry.getMediaType().toString());
            newRow.put(Constants.COL_URL, imageEntry.getUrl());
            newRow.put(Constants.COL_HD_URL, imageEntry.getHdURL());
            newRow.put(Constants.COL_COPYRIGHT, imageEntry.getCopyright());
            newRow.put(Constants.COL_IMAGE_FILE, imageEntry.getImageFile());
            return database.insert(Constants.TABLE_NAME_IMAGE_ENTRY, null, newRow);
    }

    public void deleteEntry(long id) {
        try (final SQLiteDatabase database = getImageDb(true)) {
            database.delete(Constants.TABLE_NAME_IMAGE_ENTRY, Constants.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    /**
     * Gets the results of the query in the Image table of the database.  Then calls the convertFromBlob() method
     * to convert the Blob back into an ImageEntry object, and the ImageEntry object is put
     * into the CopyOnWriteArrayList.  This is done for each row in the Cursor.
     */
    private void loadFromDB()  {
        final LoadingThread thread = new LoadingThread(getImageDb(false));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ImageEntryEntity createImageEntryEntity(final com.owen.cst2355finalproject.pojos.ImageEntry newImage) {
        final ImageEntryEntity entity = new ImageEntryEntity();
        entity.setCopyright(null);
        entity.setExplanation(newImage.getExplanation());
        entity.setId(newImage.getId());
        entity.setUrl(newImage.getUrl());
        entity.setHdURL(newImage.getHdURL());
        entity.setMediaType(MediaType.image);
        entity.setDate(newImage.getDate());
        entity.setTitle(newImage.getTitle());
        entity.setImageFile(newImage.getImageFile());
        return entity;
    }

    private void migrateV2Db() {
        final List<ImageEntry> images = new ArrayList<>(ImageInfoWrapper.getImages());
        images.sort(Comparator.comparingLong(x -> x.getId()));
        final SQLiteDatabase database = getImageDb(true);
        try {
            for (ImageEntry image : images) {
                final ImageEntryEntity entity = createImageEntryEntity(image);
                createEntryMigration(entity, database);
            }
        } finally {
            database.close();
        }
    }
}
