package com.owen.cst2355finalproject;

import android.content.Context;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ImageFragment extends Fragment {

    private Bundle activityData;
    private AppCompatActivity parentActivity;

    /*
     * inflates the layout for the fragment, and sets the parameters for the boxes.
     * the hide button will remove the fragment and pop am item off the backstack
     * to avoid having to hit back twice to go to the previous activity if you've used
     * the hide button instead of the back arrow.
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
        //Button hideButton = view.findViewById(R.id.hideButton);

        /*hideButton.setOnClickListener((click) -> {

            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

        });*/



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

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

    private void dispatchGoBrowserHDURL(String url) {

        Uri imageLocation = Uri.parse(url);
        Intent goBrowser = new Intent(Intent.ACTION_VIEW, imageLocation);
            startActivity(goBrowser);
    }
}