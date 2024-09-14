package com.owen.cst2355finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Fragment class for non-tablet size screens
 */

public class EmptyForFragment extends AppCompatActivity {

    /**
     * Inserts the fragment into the EmptyActivity Layout and
     * displays.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_for_fragment);

        final Bundle fragData = getIntent().getExtras();
        final ImageFragment imageFrag = new ImageFragment();
        imageFrag.setArguments(fragData);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentFrame, imageFrag)
                .disallowAddToBackStack()
                .commit();

    }
}