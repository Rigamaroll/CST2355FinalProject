package com.owen.cst2355finalproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CreationThread extends Thread {

    private SQLiteDatabase database;
    private ImageEntry imageEntry;

    public CreationThread(SQLiteDatabase db, ImageEntry imageEntry) {

        database = db;
        this.imageEntry = imageEntry;
    }

    @Override
    public void run() {

        ContentValues newRow = new ContentValues();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(bytesOut);
            objOut.writeObject(imageEntry);
            objOut.flush();
            objOut.close();
            bytes = bytesOut.toByteArray();
            bytesOut.flush();
            bytesOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        newRow.put(ImageDbOpener.COL_IMAGEENTRY_OBJECT, bytes);
        database.insert(ImageDbOpener.TABLE_NAME, null, newRow);
        database.close();
    }
}
