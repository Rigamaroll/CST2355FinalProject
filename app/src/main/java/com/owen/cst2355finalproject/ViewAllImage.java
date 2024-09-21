package com.owen.cst2355finalproject;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.owen.cst2355finalproject.enums.SortDirection;
import com.owen.cst2355finalproject.enums.ViewAllFilterField;
import com.owen.cst2355finalproject.enums.ViewAllSortField;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Contains the ListView for the images, and what to do when they are selected.
 */
public class ViewAllImage extends MainToolBar {

    private ImageListAdapter imageAdapter;
    private ApplicationDAO dao;
    private SortDirection currentSortDirection = SortDirection.ASC;
    private ViewAllSortField currentSortField = ViewAllSortField.SAVED_DATE;
    private ViewAllFilterField currentFilterfield = ViewAllFilterField.ALL;

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
        setImageList();
        setSpinners();
        setKeywordFilter();
    }

    private void setImageList() {
        final ListView imageList = findViewById(R.id.imageList);
        imageList.setAdapter(imageAdapter = new ImageListAdapter());
        imageList.setOnItemClickListener((p, b, pos, id) -> {
            setFragment(pos);
        });
        imageList.setOnItemLongClickListener((p, b, pos, id) -> {
            deleteListItem(id, pos);
            return true;
        });
    }

    private void setKeywordFilter() {
        final EditText keywordFilter = findViewById(R.id.keywordFilter);
        keywordFilter.setOnEditorActionListener((v, actionId, event) -> {
            ImageInfoWrapper.filterByKeywords(
                    v.getText().toString(), currentSortDirection, currentSortField, currentFilterfield);
            imageAdapter.notifyDataSetChanged();
            return true;
        });
        final Spinner filterField = findViewById(R.id.filterField);
        final ArrayAdapter<ViewAllFilterField> filterFieldAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ViewAllFilterField.values());
        filterField.setAdapter(filterFieldAdapter);
        filterField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilterfield = (ViewAllFilterField) parent.getSelectedItem();
                if(!StringUtils.isEmpty(keywordFilter.getText().toString())) {
                    ImageInfoWrapper.filterByKeywords(
                            StringUtils.trim(keywordFilter.getText().toString()),
                            currentSortDirection,
                            currentSortField,
                            currentFilterfield);
                    imageAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        final Button resetFilterButton = findViewById(R.id.resetFilterButton);
        resetFilterButton.setOnClickListener((click) -> {
            ImageInfoWrapper.filterByKeywords(StringUtils.EMPTY, currentSortDirection, currentSortField, currentFilterfield);
            keywordFilter.setText(StringUtils.EMPTY);
            imageAdapter.notifyDataSetChanged();
        });
    }

    private void setSpinners() {
        final Spinner sortField = findViewById(R.id.sortField);
        final Spinner sortOrder = findViewById(R.id.sortDirection);
        final ArrayAdapter<ViewAllSortField> sortFieldAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ViewAllSortField.values());
        final ArrayAdapter<SortDirection> sortDirectionAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SortDirection.values());
        sortField.setAdapter(sortFieldAdapter);
        sortOrder.setAdapter(sortDirectionAdapter);
        sortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSortField = (ViewAllSortField) parentView.getSelectedItem();
                ImageInfoWrapper.sortList(currentSortField,currentSortDirection);
                imageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        sortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentSortDirection = (SortDirection) parentView.getSelectedItem();
                ImageInfoWrapper.sortList(currentSortField,currentSortDirection);
                imageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Returns the Bundle containing the ImageEntry object after being inserted into the bundle.
     *
     * @param pos Index of the ImageEntry object in the CopyOnWriteArrayList.
     * @return
     */

    private Bundle getFragData(int pos) {
        final Bundle fragData = new Bundle();
        fragData.putSerializable("imageEntry", ImageInfoWrapper.getImage(pos));
        return fragData;
    }

    /**
     * Sets the fragments for the ImageEntry by first determining which layout is being run.
     *
     * @param pos the index number in the CopyOnWriteArrayList storing the ImageEntry objects
     */

    private void setFragment(int pos) {
        if (findViewById(R.id.fragmentFrame) != null) {
            final ImageFragment imageFrag = new ImageFragment();
            imageFrag.setArguments(getFragData(pos));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentFrame, imageFrag)
                    .addToBackStack(null)
                    .commit();
        } else {
            final Intent seeImageInfo = new Intent(ViewAllImage.this, EmptyForFragment.class);
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Constants.DELETE_DIALOG_TEXT)
                .setMessage(String.format(
                        Constants.DELETE_IMAGE_INFO,
                        ImageInfoWrapper.getImage(pos).getTitle(),
                        ImageInfoWrapper.getImage(pos).getDate()))
                .setPositiveButton("Yes", (click, arg) -> {
                    final Fragment imageFrag = getSupportFragmentManager().findFragmentById(R.id.imageTitle);
                    if (imageFrag != null) {
                        getSupportFragmentManager().beginTransaction().remove(imageFrag).commit();
                    }
                    dao.deleteEntry(id);
                    ImageInfoWrapper.deleteImage(pos);
                    imageAdapter.notifyDataSetChanged();
                    Toast.makeText(this, R.string.deleteToast, Toast.LENGTH_LONG)
                    .show();
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
            return ImageInfoWrapper.getImage(position);
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
            final LayoutInflater inflater = getLayoutInflater();
            final ImageEntry imageFile = ImageInfoWrapper.getImage(position);
            final View view = inflater.inflate(R.layout.image_list, parent, false);

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