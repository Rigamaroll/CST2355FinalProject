package com.owen.cst2355finalproject;

import android.database.sqlite.SQLiteDatabase;

public class DeleterThread extends Thread {

    private final SQLiteDatabase database;
    private long id;

    public DeleterThread(SQLiteDatabase db, long id) {
        this.database = db;
        this.id = id;
    }

    public void run() {
        database.delete(Constants.TABLE_NAME, Constants.COL_ID + " = ?", new String[]{String.valueOf(id)});
        database.close();
    }
}
