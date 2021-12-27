package com.owen.cst2355finalproject;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Contains the ListView for the images, and what to do when they are selected.
 */
public class ViewAllImage extends MainToolBar {

    private ImageListAdapter imageAdapter;
    private ImageInfoWrapper wrap;

    /**
     * Initializes the Toolbar, NavigationDrawer, and NavigationView, then initializes
     * the ListView with adapter, checks the screen resolution for Fragment decisions, and sets
     * the listener to set the fragments if the photo is clicked.  If a long click occurs on the image will
     * give choice to delete the image.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_image);
        wrap = new ImageInfoWrapper(this);
        initialize();
        getToolbar().setTitle(R.string.viewAllImageTitle);

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

        imageList.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")

                    .setMessage("The selected image is: " + wrap.getImages(pos).getTitle() + "\n" + "The database id is: " + id)

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

                    .setNegativeButton("No", (click, arg) -> {
                    })

                    .create().show();
            return true;
        });
    }

    /**
     * Returns the Bundle containing the ImageEntry object after being inserted into the bundle.
     *
     * @param pos Index of the ImageEntry object in the CopyOnWriteArrayList.
     * @return
     */

    private Bundle getFragData(int pos) {

        Bundle fragData = new Bundle();
        fragData.putSerializable("imageEntry", wrap.getImages(pos));
        return fragData;
    }

    /**
     * Adapter for the ListView so that the images will be displayed properly in the ListView
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

        /**
         * the getView method of the adapter sets the Image and the Title of the image in the list
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            ImageEntry imageFile = wrap.getImages(position);

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