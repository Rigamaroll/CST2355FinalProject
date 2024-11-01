package com.owen.cst2355finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.owen.cst2355finalproject.enums.MediaType;
import com.owen.cst2355finalproject.pojos.ImageEntry;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Fragment class that holds the image information being displayed in the EmptyForFragment,
 * or in the frame in the 720w layout.
 */

public class ImageFragment extends Fragment {

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

        final Bundle activityData = getArguments();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        final TextView fragmentTitle = view.findViewById(R.id.fragmentTitle);
        final TextView fragmentDate = view.findViewById(R.id.fragmentDate);
        final TextView fragmentExplanation = view.findViewById(R.id.fragmentExplanation);
        final TextView fragmentURL = view.findViewById(R.id.fragmentURL);
        final TextView fragmentHdURL = view.findViewById(R.id.fragmentHdURL);
        final TextView fragmentCopyright = view.findViewById(R.id.fragmentCopyright);
        final ImageView fragmentImage = view.findViewById((R.id.imageForFragment));

        final ImageEntry bundleImage = (ImageEntry) activityData.getSerializable("imageEntry");

        fragmentTitle.append(bundleImage.getTitle());
        fragmentDate.append(bundleImage.getDate());
        fragmentExplanation.append(bundleImage.getExplanation());
        fragmentURL.append(bundleImage.getUrl());
        if (bundleImage.getMediaType() == MediaType.IMAGE) {
            fragmentHdURL.append(bundleImage.getHdURL());
        } else {
            fragmentHdURL.setText("Video Link: \n" + bundleImage.getUrl());
        }
        final String copyright = bundleImage.getCopyright() != null
                ? StringUtils.trim(bundleImage.getCopyright()) : getString(R.string.notAvailable);
        fragmentCopyright.append(copyright);

        try {
            fragmentImage.setImageBitmap(bundleImage.getImageFileAsBitMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fragmentHdURL.setOnClickListener((click) -> {
            final String url = bundleImage.getMediaType() == MediaType.IMAGE
                    ? bundleImage.getHdURL() : bundleImage.getUrl();
            dispatchGoBrowserHDURL(url);
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Opens the browser with the URL from the HD image in the Fragment
     *
     * @param url location of the image
     */

    private void dispatchGoBrowserHDURL(String url) {
        final Uri imageLocation = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, imageLocation));
    }
}