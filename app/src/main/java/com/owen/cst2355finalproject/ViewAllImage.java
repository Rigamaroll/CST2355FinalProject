package com.owen.cst2355finalproject;

import static java.sql.DriverManager.getConnection;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

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

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ViewAllImage extends AppCompatActivity {

    private ImageListAdapter imageAdapter;
    private ImageInfoWrapper wrap;
    MainToolBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_image);
        wrap = new ImageInfoWrapper(this);
        toolbar = new MainToolBar(this, this);
        toolbar.getToolbar().setTitle(R.string.viewAllImageTitle);

        ListView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(imageAdapter = new ImageListAdapter());

        boolean isTablet = findViewById(R.id.fragmentFrame) != null;

        imageList.setOnItemClickListener((p, b, pos, id) -> {

            if (isTablet) {

                ImageFragment imageFrag = new ImageFragment();
                imageFrag.setArguments(getFragData(pos));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentFrame, imageFrag)
                        .addToBackStack(null)
                        .commit();

            } else {

                Intent seeImageInfo = new Intent(ViewAllImage.this, EmptyForFragment.class);
                seeImageInfo.putExtras(getFragData(pos));
                startActivity(seeImageInfo);
            }
        });

        /*
        the longItemClickListener which determines what happens when a long
        click occurs on the objects in the ListView.  No button does nothing.
        Yes button deletes the message from the ListView and the database, and it
        removes the fragment from the frame if it's a tablet.
         */

            imageList.setOnItemLongClickListener((p, b, pos, id) -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Do you want to delete this?")

                        //What is the message:
                        .setMessage("The selected image is: " + wrap.getImages(pos).getTitle() + "\n" + "The database id is: " + id)

                        //what the Yes button does:
                        .setPositiveButton("Yes", (click, arg) -> {

                            Fragment imageFrag = getSupportFragmentManager().findFragmentById(R.id.imageTitle);
                            if (imageFrag != null) {

                                getSupportFragmentManager().beginTransaction().remove(imageFrag).commit();

                            }

                            wrap.getImageDb(true).delete(ImageDbOpener.TABLE_NAME, ImageDbOpener.COL_ID + " = ?",
                                    new String[]{String.valueOf(id)});
                            wrap.deleteImages(pos);
                            imageAdapter.notifyDataSetChanged();

                        })
                        //What the No button does:
                        .setNegativeButton("No", (click, arg) -> {
                        })

                        //Show the dialog
                        .create().show();
                return true;
            });
    }

    private Bundle getFragData(int pos) {

        Bundle fragData = new Bundle();
        fragData.putSerializable("imageEntry", wrap.getImages(pos));
        return fragData;
    }

    /*
    the layout adapter class for the ListView to make the chat window work.
     */
    private class ImageListAdapter extends BaseAdapter {

        public int getCount() {

            return wrap.listSize();
        }

        public ImageEntry getItem(int position) {

            return wrap.getImages(position);
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
            ImageEntry imageFile = wrap.getImages(position);

            // Depending if the message is sent or received, load the correct template
            View view = inflater.inflate(R.layout.image_list, parent, false);

            Bitmap image = null;
            try {
                image = imageFile.getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            ((ImageView) view.findViewById(R.id.imageItem)).setImageBitmap(image);
            ((TextView) view.findViewById(R.id.imageTitle)).setText(imageFile.getTitle());

            return view;
        }
    }
}