package com.owen.cst2355finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;

public class MainToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggleDrawer;
    NavigationView navView;

    //public MainToolBar(@NonNull Context context, AppCompatActivity active, Toolbar tools, DrawerLayout drawer, NavigationView navView) {

    public MainToolBar(@NonNull Context context, AppCompatActivity active) {
        this.context = context;
        //this.toolbar = tools;
        this.drawer = drawer;
        navView = active.findViewById(R.id.navView);
        toolbar = active.findViewById(R.id.mainToolBar);
        drawer = active.findViewById(R.id.navDrawer);
        this.toggleDrawer = new ActionBarDrawerToggle(active, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();
        //this.navView = navView;
        navView.setNavigationItemSelectedListener(this);

    }

    public Toolbar getToolbar() {

        return toolbar;
    }

    public DrawerLayout getDrawer() {

        return this.drawer;
    }

    public NavigationView getNavView() {

        return this.navView;
    }

    public ActionBarDrawerToggle getToggleDrawer() {
        return toggleDrawer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return this.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String toastMessage = null;
        Snackbar funnySnacks = null;

        switch (item.getItemId()) {

            case R.id.item1:
                toastMessage = getString(R.string.open);
                break;
            case R.id.item2:
                toastMessage = getString(R.string.close);
                break;
            case R.id.item3:
                //toastMessage = getString(R.string.toolBarOctopus);
                break;
        }


        Toast.makeText(this.context, toastMessage, Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.goHome:
                Intent goDash = new Intent(context, Dashboard.class);
                context.startActivity(goDash);
                break;

            case R.id.goSearchPage:
                Intent goSearch = new Intent(context, SearchImage.class);
                context.startActivity(goSearch);
                break;

            case R.id.goViewImagePage:
                Intent goViewAll = new Intent(context, ViewAllImage.class);
                context.startActivity(goViewAll);
                break;

            case R.id.logout:
                Intent logout = new Intent(context, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(logout);

                break;
        }

        return false;
    }
}