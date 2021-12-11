package com.owen.cst2355finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toolbar);

        Toolbar tools = findViewById(R.id.mainToolBar);

        setSupportActionBar(tools);

        DrawerLayout drawer = findViewById(R.id.navDrawer);
        ActionBarDrawerToggle toggleDrawer = new ActionBarDrawerToggle(this, drawer, tools, R.string.open, R.string.close);
        drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();

        NavigationView navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.goHome:
                Intent goDash = new Intent(this, Dashboard.class);
                startActivity(goDash);
                break;

            case R.id.goSearchPage:
                Intent goSearch = new Intent(getApplicationContext(), SearchImage.class);
                startActivity(goSearch);
                break;

            case R.id.goViewImagePage:
                Intent goViewAll = new Intent(this, ViewAllImage.class);
                startActivity(goViewAll);
                break;

            case R.id.logout:
                Intent logout = new Intent(this, MainActivity.class);
                startActivity(logout);
                break;
        }

        return false;
    }
}