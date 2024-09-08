package com.owen.cst2355finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CreationThread extends Thread {

    private final SQLiteDatabase database;
    private ImageEntry imageEntry;

    public CreationThread(SQLiteDatabase db, ImageEntry imageEntry) {
        database = db;
        this.imageEntry = imageEntry;
    }

    @Override
    public void run() {

        final ContentValues newRow = new ContentValues();
        byte[] bytes = null;
        try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
             final ObjectOutputStream objOut = new ObjectOutputStream(bytesOut)) {
            objOut.writeObject(imageEntry);
            objOut.flush();
            bytes = bytesOut.toByteArray();
            bytesOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        newRow.put(Constants.COL_IMAGE_ENTRY_OBJECT, bytes);
        database.insert(Constants.TABLE_NAME, null, newRow);
        database.close();
    }
}
