package com.owen.cst2355finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MainToolBar implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar tools;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggleDrawer;
    NavigationView navView;
    Activity current;

    public MainToolBar(Activity current, View tools, View drawer, View navView) {

        this.current = current;
        this.tools = (Toolbar)tools;
        this.drawer = (DrawerLayout) drawer;
        this.navView = (NavigationView) navView;
        toggleDrawer = new ActionBarDrawerToggle(current, this.drawer, this.tools, R.string.open, R.string.close);
    }

    public Toolbar getTools() {
        return tools;
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public ActionBarDrawerToggle getToggleDrawer() {
        return toggleDrawer;
    }

    public NavigationView getNavView() {
        return navView;
    }

    /*@Override
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

    }*/


    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.goHome:
                Intent goDash = new Intent(current, Dashboard.class);
                current.startActivity(goDash);
                break;

            case R.id.goSearchPage:
                Intent goSearch = new Intent(current, SearchImage.class);
                current.startActivity(goSearch);
                break;

            case R.id.goViewImagePage:
                Intent goViewAll = new Intent(current, ViewAllImage.class);
                current.startActivity(goViewAll);
                break;

            case R.id.logout:
                Intent logout = new Intent(current, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                current.startActivity(logout);

                break;
        }

        return false;
    }
}