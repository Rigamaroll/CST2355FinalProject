package com.owen.cst2355finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * The class which contains the ActionBar and NavigationDrawer information. All the other activities
 * extend this class for functionality.
 */

public class MainToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggleDrawer;
    NavigationView navView;

    public MainToolBar() {

    }

    /**
     * called by any object extending this class to initialize the bar
     * and drawer.
     */

    public void initialize() {

        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.mainToolBar);
        drawer = findViewById(R.id.navDrawer);
        setSupportActionBar(toolbar);
        toggleDrawer = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();
        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * @return the toolbar the activity uses.
     */

    public Toolbar getToolbar() {

        return toolbar;
    }

    /**
     * @return the DrawerLayout being used
     */
    public DrawerLayout getDrawer() {

        return this.drawer;
    }

    /**
     * @return the NavigationView being used
     */
    public NavigationView getNavView() {

        return this.navView;
    }

    /**
     * @return the ActionBarDrawerToggle being used
     */
    public ActionBarDrawerToggle getToggleDrawer() {
        return toggleDrawer;
    }

    /**
     * inflates the actionbar Menu
     *
     * @param menu actionbar menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * when the item on the ActionBar is pressed it will run the appropriate item.
     * For this app, there is only one button for help which creates a help AlertDialog.
     *
     * @param item clicked item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String alertText = helpAlert();

        if  (String.valueOf(item.getItemId()).contentEquals(String.valueOf(R.id.helpScreen))) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.helpAlert)
                        .setMessage(alertText)
                        .setPositiveButton(R.string.ok, (click, arg) -> {

                        })
                        .create()
                        .show();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.goHome:
                drawer.close();
                Intent goDash = new Intent(this, Dashboard.class);
                startActivity(goDash);
                return true;

            case R.id.goSearchPage:
                drawer.close();
                Intent goSearch = new Intent(this, SearchImage.class);
                startActivity(goSearch);
                return true;

            case R.id.goViewImagePage:
                drawer.close();
                Intent goViewAll = new Intent(this, ViewAllImage.class);
                startActivity(goViewAll);
                return true;

            case R.id.logout:
                drawer.close();
                Intent logout = new Intent(this, MainActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
                return true;
        }

        return false;
    }

    /**
     * Generates the help alert String to be displayed for each activity.
     *
     * @return help alert string
     */

    public String helpAlert() {

        String className = getClass().toString().toLowerCase();
        String alertText = "";

        switch (className) {

            case "class com.owen.cst2355finalproject.dashboard":

                alertText = getString(R.string.helpDashboard);
                break;

            case "class com.owen.cst2355finalproject.mainactivity":

                alertText = getString(R.string.helpLogin);
                break;

            case "class com.owen.cst2355finalproject.searchimage":

                alertText = getString(R.string.helpSearchImage);
                break;

            case "class com.owen.cst2355finalproject.viewallimage":

                alertText = getString(R.string.helpViewAllImage);
                break;

        }
        return alertText;

    }
}