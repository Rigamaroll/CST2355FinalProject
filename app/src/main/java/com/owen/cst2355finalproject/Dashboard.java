package com.owen.cst2355finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Dashboard extends AppCompatActivity {

    MainToolBar toolbar;
    ImageInfoWrapper wrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        String lastLogin = getIntent().getStringExtra("lastLogin");
        wrap = new ImageInfoWrapper(this);

        toolbar = new MainToolBar(this, this);
        toolbar.getToolbar().setTitle(R.string.dashBoardTitle);

        TextView totalPhotos = findViewById(R.id.totalPhotos);
        totalPhotos.append(String.valueOf(wrap.listSize()));
        TextView lastLog = findViewById(R.id.lastLogin);
        lastLog.append(lastLogin == null ? "" : lastLogin);
    }
}