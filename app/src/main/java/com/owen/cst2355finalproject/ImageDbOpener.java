package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Class for accessing the Database.
 */

public class ImageDbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ImageDB";
    protected final static int VERSION_NUM = 4;

    private Context context;

    public ImageDbOpener(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
        this.context = context;
    }

    /**
     * Creates the database if it doesn't already exist.
     *
     * @param imageDB
     */
    @Override
    public void onCreate(SQLiteDatabase imageDB) {
        try {
            runCreateScript(imageDB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * if the version number gets upgraded this gets called.
     *
     * @param imageDB
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase imageDB, int oldVersion, int newVersion) {
        try {
            runUpgradeScript(imageDB, oldVersion, newVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCreateScript(final SQLiteDatabase imageDB) throws IOException {
       final String sql = generateSqlFromFile("create.sql");
       imageDB.execSQL(sql);
    }

    private void runUpgradeScript(
            final SQLiteDatabase imageDB,
            final int oldVersion,
            final int newVersion) throws IOException {
        for (int i = oldVersion; i < newVersion; i++) {
            final String fileName = String.format("%d-%d-upgrade.sql", i,i+1);
            final String sql = generateSqlFromFile(fileName);
            imageDB.execSQL(sql);
        }
    }

    private String generateSqlFromFile(final String fileName) throws IOException {
        try (final BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     context.getAssets().open(fileName)))) {
            return reader
                    .lines()
                    .collect(Collectors.joining());
        }
    }
}
