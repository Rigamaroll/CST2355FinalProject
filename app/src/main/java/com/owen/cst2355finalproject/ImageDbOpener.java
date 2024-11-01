package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Class for accessing the Database.
 */

public class ImageDbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ImageDB";
    protected final static int VERSION_NUM = 5;

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
            imageDB.beginTransaction();
            runCreateScript(imageDB);
            imageDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed Creating new Database");
        } finally {
            imageDB.endTransaction();
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
            imageDB.beginTransaction();
            runUpgradeScript(imageDB, oldVersion, newVersion);
            imageDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed Upgrading Database");
        } finally {
            imageDB.endTransaction();
        }
    }

    private void runCreateScript(final SQLiteDatabase imageDB) throws IOException {
       final List<String> sqlList = generateSqlFromFile("create.sql");
       executeAllSqlInFile(sqlList, imageDB);
    }

    private void runUpgradeScript(
            final SQLiteDatabase imageDB,
            final int oldVersion,
            final int newVersion) throws IOException {
        for (int i = oldVersion; i < newVersion; i++) {
            final String fileName = String.format("%d-%d-upgrade.sql", i,i+1);
            final List<String> sqlList = generateSqlFromFile(fileName);
            executeAllSqlInFile(sqlList, imageDB);
        }
    }

    private List<String> generateSqlFromFile(final String fileName) throws IOException {
        try (final BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     context.getAssets().open(fileName)))) {
            final String allSql = reader
                    .lines()
                    .collect(Collectors.joining());
            return Arrays.asList(StringUtils.split(allSql, ";"));
        }
    }

    private void executeAllSqlInFile(final List<String> sqlList, final SQLiteDatabase imageDB) {
        for (final String sql : sqlList) {
            imageDB.execSQL(sql);
        }
    }
}
