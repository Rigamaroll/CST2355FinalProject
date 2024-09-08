package com.owen.cst2355finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ApplicationDAO {

    private ImageDbOpener dbOpener;

    public ApplicationDAO(Context context) {
        dbOpener = new ImageDbOpener(context);
        if (ImageInfoWrapper.listSize() == 0) {
            loadFromDB();
        }
    }

    public SQLiteDatabase getImageDb(boolean type) {
        return type ? dbOpener.getWritableDatabase() : dbOpener.getReadableDatabase();
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

    public void createEntry(ImageEntry imageEntry) {
        final ContentValues newRow = new ContentValues();
        byte[] bytes = null;
        try (final SQLiteDatabase database = getImageDb(true);
                final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
             final ObjectOutputStream objOut = new ObjectOutputStream(bytesOut)) {
            objOut.writeObject(imageEntry);
            objOut.flush();
            bytes = bytesOut.toByteArray();
            bytesOut.flush();
            newRow.put(Constants.COL_IMAGE_ENTRY_OBJECT, bytes);
            database.insert(Constants.TABLE_NAME, null, newRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteEntry(long id) {
        try (final SQLiteDatabase database = getImageDb(true)) {
            database.delete(Constants.TABLE_NAME, Constants.COL_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    /**
     * Gets the results of the query in the Image table of the database.  Then calls the convertFromBlob() method
     * to convert the Blob back into an ImageEntry object, and the ImageEntry object is put
     * into the CopyOnWriteArrayList.  This is done for each row in the Cursor.
     */
    private void loadFromDB() {
        new LoadingThread(getImageDb(false)).start();
    }
}
