package com.owen.cst2355finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
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
    Activity active;

    public MainToolBar(@NonNull Context context, AppCompatActivity active) {

        this.context = context;
        this.active = active;
        navView = active.findViewById(R.id.navView);
        toolbar = active.findViewById(R.id.mainToolBar);
        drawer = active.findViewById(R.id.navDrawer);
        toggleDrawer = new ActionBarDrawerToggle(active, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();
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

        String alertString = null;

        switch (item.getItemId()) {

            /*case R.id.helpScreen:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.dateOutRange)
                        .setMessage(alertString)
                        .setPositiveButton(R.string.yes, (click, arg) -> {
                            datePicker.show();
                        })
                        .setNegativeButton(R.string.no, (click, arg) -> {
                        })
                        .create()
                        .show();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.goHome:
                drawer.close();
                Intent goDash = new Intent(context, Dashboard.class);
                context.startActivity(goDash);
                break;

            case R.id.goSearchPage:
                drawer.close();
                Intent goSearch = new Intent(context, SearchImage.class);
                context.startActivity(goSearch);
                break;

            case R.id.goViewImagePage:
                drawer.close();
                Intent goViewAll = new Intent(context, ViewAllImage.class);
                context.startActivity(goViewAll);
                break;

            case R.id.logout:
                drawer.close();
                Intent logout = new Intent(context, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(logout);
                break;
        }

        return false;
    }
}