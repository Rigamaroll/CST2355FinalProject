package com.owen.cst2355finalproject;

import static java.sql.DriverManager.getConnection;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ViewAllImage extends AppCompatActivity {
    private CopyOnWriteArrayList<ImageEntry> storedImageList = new CopyOnWriteArrayList<ImageEntry>();
    private ImageListAdapter imageAdapter;
    SQLiteDatabase imageDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_image);

        Button searchImage = findViewById(R.id.goToSearchImage);
            searchImage.setOnClickListener((click) ->{

                Intent goSearchPage = new Intent(this, SearchImage.class);

            });
        ListView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(imageAdapter = new ImageListAdapter());
        loadFromDB();

        imageList.setOnItemClickListener((p, b, pos, id) -> {

            /*if (isTablet) {

                DetailsFragment messageFrag = new DetailsFragment();
                messageFrag.setArguments(getFragData(pos, id));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentFrame, messageFrag)
                        .addToBackStack(null)
                        .commit();

            } else {

                Intent seeImageInfo = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                seeImageInfo.putExtras(getFragData(pos, id));
                startActivity(seeImageInfo);
            }*/
        });
    }

    /*
    the layout adapter class for the ListView to make the chat window work.
     */
    private class ImageListAdapter extends BaseAdapter {

        public int getCount() {

            return storedImageList.size();
        }

        public ImageEntry getItem(int position) {

            return storedImageList.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        /*
        the getView method of the adapter checks the boolean value of isSent
        to see which side of the layout it should place the new item.
         */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            ImageEntry imagefile = storedImageList.get(position);
            System.out.println (imagefile.toString());
            System.out.println("got to getView");

            // Depending if the message is sent or received, load the correct template
            View view = inflater.inflate(R.layout.image_list, parent, false);

            Bitmap image = null;
            try {
                image = imagefile.getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            ((ImageView) view.findViewById(R.id.imageItem)).setImageBitmap(image);
            ((TextView) view.findViewById(R.id.imageTitle)).setText(imagefile.getTitle());

            return view;
        }
    }

    private void loadFromDB() {

        //get a database connection:
        ImageDbOpener dbOpener = new ImageDbOpener(this);
        imageDB = dbOpener.getWritableDatabase();

        //query all the results from the database:
        Cursor results = imageDB.rawQuery("SELECT " + ImageDbOpener.COL_ID + ", "
                + ImageDbOpener.COL_IMAGEENTRY_OBJECT + " FROM IMAGE;", null);
                /*+ ", " + ImageDbOpener.COL_DATE
                + ImageDbOpener.COL_URL + ", " + ImageDbOpener.COL_HD_URL + "  FROM IMAGE", null);
*/
        //find the column indices:

        int idIndex = results.getColumnIndex(ImageDbOpener.COL_ID);
        int imageObject = results.getColumnIndex(ImageDbOpener.COL_IMAGEENTRY_OBJECT);

        //log the results

       // printCursor(results, imageDB.getVersion());

        //After passing the cursor to the logging method, I had to reset the location of the cursor

      /*  results.moveToFirst();
        results.moveToPrevious();*/

        //iterate over the results, return true if there is a next item:

        while (results.moveToNext()) {

            byte[] imageEntryObject = results.getBlob(imageObject);
            ImageEntry newImageEntry = convertFromBlob(imageEntryObject);
            long id = results.getLong(idIndex);
            //add message to ArrayList
            storedImageList.add(newImageEntry);
        }
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