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
import android.widget.Toast;

import java.io.IOException;

/**
 * Contains the ListView for the images, and what to do when they are selected.
 */
public class ViewAllImage extends MainToolBar {

    private ImageListAdapter imageAdapter;
    private ApplicationDAO dao;

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
        dao = new ApplicationDAO(this);
        initialize();
        getToolbar().setTitle(R.string.viewAllImageTitle);

        ListView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(imageAdapter = new ImageListAdapter());

        imageList.setOnItemClickListener((p, b, pos, id) -> {

            setFragment(pos);
        });

        imageList.setOnItemLongClickListener((p, b, pos, id) -> {

            deleteListItem(id, pos);
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
        fragData.putSerializable("imageEntry", ImageInfoWrapper.getImages(pos));
        return fragData;
    }

    /**
     * Sets the fragments for the ImageEntry by first determining which layout is being run.
     *
     * @param pos the index number in the CopyOnWriteArrayList storing the ImageEntry objects
     */

    private void setFragment(int pos) {

        boolean isTablet = findViewById(R.id.fragmentFrame) != null;

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
    }

    /**
     * Creates the AlertDialog to decide if delete the item or not.
     *
     * @param id  database id number for finding row to delete
     * @param pos the index number in the CopyOnWriteArrayList storing the ImageEntry objects
     */
    private void deleteListItem(long id, int pos) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Do you want to delete this?")

                .setMessage("The selected image is: " + ImageInfoWrapper.getImages(pos).getTitle() + "\n" + "The database id is: " + id)
                .setPositiveButton("Yes", (click, arg) -> {

                    Fragment imageFrag = getSupportFragmentManager().findFragmentById(R.id.imageTitle);

                    if (imageFrag != null) {

                        getSupportFragmentManager().beginTransaction().remove(imageFrag).commit();
                    }

                    dao.deleteEntry(id);
                    ImageInfoWrapper.deleteImages(pos);
                    imageAdapter.notifyDataSetChanged();
                    Toast deleteToast = Toast.makeText(this, R.string.deleteToast, Toast.LENGTH_LONG);
                    deleteToast.show();
                })

                .setNegativeButton("No", (click, arg) -> {
                })

                .create().show();
    }

    /**
     * Adapter for the ListView so that the images will be displayed properly in the ListView
     */
    private class ImageListAdapter extends BaseAdapter {

        public int getCount() {

            return ImageInfoWrapper.listSize();
        }

        public ImageEntry getItem(int position) {

            return ImageInfoWrapper.getImages(position);
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
            ImageEntry imageFile = ImageInfoWrapper.getImages(position);

            View view = inflater.inflate(R.layout.image_list, parent, false);

            Bitmap image = null;
            try {
                image = imageFile.getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ((ImageView) view.findViewById(R.id.imageItem)).setImageBitmap(image);
            ((TextView) view.findViewById(R.id.imageTitle)).setText(imageFile.getTitle());

            return view;
        }
    }

}