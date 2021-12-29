package com.owen.cst2355finalproject;

import android.database.sqlite.SQLiteDatabase;

public class DeleterThread extends Thread {

    private SQLiteDatabase database;
    private long id;

    public DeleterThread(SQLiteDatabase db, long id) {

        this.database = db;
        this.id = id;
    }

    public void run() {

        database.delete(ImageDbOpener.TABLE_NAME, ImageDbOpener.COL_ID + " = ?", new String[]{String.valueOf(id)});
        database.close();

    }

}
