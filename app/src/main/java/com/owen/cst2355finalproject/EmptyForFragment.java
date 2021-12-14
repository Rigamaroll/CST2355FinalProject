package com.owen.cst2355finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyForFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_for_fragment);

        Bundle fragData = getIntent().getExtras();

        ImageFragment imageFrag = new ImageFragment();
        imageFrag.setArguments(fragData);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentFrame, imageFrag)
                .disallowAddToBackStack()
                .commit();

    }
}