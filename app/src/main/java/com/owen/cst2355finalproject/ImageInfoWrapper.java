package com.owen.cst2355finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageInfoWrapper {

    private static CopyOnWriteArrayList<ImageEntry> images;
    private ImageDbOpener opener;
    private SQLiteDatabase imageDb;
    private Context context;

    public ImageInfoWrapper(Context context) {

        this.images = new CopyOnWriteArrayList<ImageEntry>();
        this.context = context;
        opener = new ImageDbOpener(this.context);
        if (images.size() == 0) {

            loadFromDB();
        }
    }

    public ImageEntry getImages(int position) {
        return images.get(position);
    }

    public void setImages(ImageEntry image) {
        images.add(image);
    }

    public void deleteImages(int position) {

        images.remove(position);
    }

    public int listSize() {

        return images.size();
    }

    public void setOpenerContext (Context context) {

        this.opener = new ImageDbOpener(context);
    }

    public SQLiteDatabase getImageDb(boolean type) {

        SQLiteDatabase imageDb = type ? opener.getWritableDatabase() : opener.getReadableDatabase();
        return imageDb;
    }

    private void loadFromDB() {

        //get a database connection:
        //ImageDbOpener dbOpener = new ImageDbOpener(this);
        //imageDB = wrap.getImageDb(true);

        //query all the results from the database:
        Cursor results = getImageDb(true).rawQuery("SELECT " + ImageDbOpener.COL_ID + ", "
                + ImageDbOpener.COL_IMAGEENTRY_OBJECT + " FROM IMAGE;", null);

        //find the column indices:

        int idIndex = results.getColumnIndex(ImageDbOpener.COL_ID);
        int imageObject = results.getColumnIndex(ImageDbOpener.COL_IMAGEENTRY_OBJECT);

        //iterate over the results, return true if there is a next item:

        while (results.moveToNext()) {

            byte[] imageEntryObject = results.getBlob(imageObject);
            ImageEntry newImageEntry = convertFromBlob(imageEntryObject);
            long id = results.getLong(idIndex);
            //add message to ArrayList
            setImages(newImageEntry);
        }
        results.close();
        getImageDb(true).close();
    }

    public ImageEntry convertFromBlob(byte[] imageEntryObject) {
        ImageEntry newImageEntry = null;
        try {
            ByteArrayInputStream imageInput = new ByteArrayInputStream(imageEntryObject);
            ObjectInputStream newImage = new ObjectInputStream(imageInput);
            newImageEntry = (ImageEntry) newImage.readObject();
            newImage.close();
            imageInput.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newImageEntry;
    }
}
