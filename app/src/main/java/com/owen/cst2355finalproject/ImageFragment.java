package com.owen.cst2355finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Fragment class that holds the image information being displayed in the EmptyForFragment,
 * or in the frame in the 720w layout.
 */

public class ImageFragment extends Fragment {

    private Bundle activityData;
    private AppCompatActivity parentActivity;

    /**
     * Inflates the layout for the fragment, and sets the parameters for the boxes.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activityData = getArguments();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        TextView fragmentTitle = view.findViewById(R.id.fragmentTitle);
        TextView fragmentDate = view.findViewById(R.id.fragmentDate);
        TextView fragmentExplanation = view.findViewById(R.id.fragmentExplanation);
        TextView fragmentURL = view.findViewById(R.id.fragmentURL);
        TextView fragmentHdURL = view.findViewById(R.id.fragmentHdURL);
        ImageView fragmentImage = view.findViewById((R.id.imageForFragment));

        ImageEntry bundleImage = (ImageEntry) activityData.getSerializable("imageEntry");

        fragmentTitle.append(bundleImage.getTitle());
        fragmentDate.append(bundleImage.getDate());
        fragmentExplanation.append(bundleImage.getExplanation());
        fragmentURL.append(bundleImage.getUrl());
        fragmentHdURL.append(bundleImage.getHdURL());

        try {
            fragmentImage.setImageBitmap(bundleImage.getImageFile());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        fragmentHdURL.setOnClickListener((click) -> {

            dispatchGoBrowserHDURL(bundleImage.getHdURL());
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (AppCompatActivity) context;
    }

    /**
     * Opens the browser with the URL from the HD image in the Fragment
     *
     * @param url location of the image
     */

    private void dispatchGoBrowserHDURL(String url) {

        Uri imageLocation = Uri.parse(url);
        Intent goBrowser = new Intent(Intent.ACTION_VIEW, imageLocation);
        startActivity(goBrowser);
    }
}