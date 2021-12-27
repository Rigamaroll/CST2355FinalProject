package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApplicationDAO {

    private ImageDbOpener dbOpener;

    public ApplicationDAO (Context context) {

        dbOpener = new ImageDbOpener(context);
        if (ImageInfoWrapper.listSize() == 0) {

            loadFromDB();
        }

    }

    public SQLiteDatabase getImageDb(boolean type) {

        SQLiteDatabase imageDb = type ? dbOpener.getWritableDatabase() : dbOpener.getReadableDatabase();
        return imageDb;
    }

    public long getNextKeyNumber() {
        long id = 0;
        Cursor results = getImageDb(true).rawQuery("SELECT max(seq) FROM sqlite_sequence;", null, null);
        while (results.moveToNext()) {

            id = results.getLong(0) + 1;
        }
        if (results != null) {
            results.close();
        }

       return id;
    }

    public void createEntry (ImageEntry imageEntry) {

        new CreationThread(getImageDb(true), imageEntry).start();
    }

    public void deleteEntry(long id) {

        new DeleterThread(getImageDb(true), id).start();

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
