package com.owen.cst2355finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Button toolbarButton = findViewById(R.id.goToMainToolbar);
        toolbarButton.setOnClickListener((click) -> {

            goActivity("toolbar");

        });

        final Button goSearchButton = findViewById(R.id.goToSearchImage);
        goSearchButton.setOnClickListener((click) -> {

            goActivity("search");

        });

        final Button goViewAllButton = findViewById(R.id.goToViewAllImages);
        goViewAllButton.setOnClickListener((click) -> {

            goActivity("view");

        });

    }

    private void goActivity (String activity) {
        Intent goActivity = null;
        switch (activity) {

            case "toolbar":
                goActivity = new Intent(this, MainToolBar.class);
                break;
            case "view":
                goActivity = new Intent(this, ViewAllImage.class);
                break;
            case "search":
                goActivity = new Intent(this, SearchImage.class);
                break;
        }
        startActivity(goActivity);

    }
}