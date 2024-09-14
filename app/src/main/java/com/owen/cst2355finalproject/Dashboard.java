package com.owen.cst2355finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Landing page after login.  Shows statistics about your app usage.
 */
public class Dashboard extends MainToolBar {

    private static String lastLogin;
    private final String DEFAULT_API_KEY = "DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d";

    /**
     * Initializes the toolbar, navigation drawer, and menus.  Checks the intent for the last
     * login date and stores that in a static variable so it won't be lost if activities are
     * changed.  Checks the shared preference for the API key and displays it.  Also allows
     * changing of the API key being used by entering a new one and setting it.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (!(getIntent().getStringExtra("lastLogin") == null)) {
            lastLogin = getIntent().getStringExtra("lastLogin");
        }

        initialize();
        getToolbar().setTitle(R.string.dashBoardTitle);

        final TextView totalPhotos = findViewById(R.id.totalPhotos);
        totalPhotos.append(String.valueOf(ImageInfoWrapper.listSize()));
        final TextView lastLog = findViewById(R.id.lastLogin);
        lastLog.append(lastLogin == null ? "" : lastLogin);

        final EditText api = findViewById(R.id.personalAPI);
        api.setText(getSharedPreferences("apiKey", MODE_PRIVATE).getString("key", ""));

        checkEmptyAPI(api);

        final Button setAPI = findViewById(R.id.setAPI);
        setAPI.setOnClickListener((click) -> {
            setAPI(api);
            checkEmptyAPI(api);
            Toast.makeText(this, R.string.apiKeySet, Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Sets the new API key in the shared preferences
     *
     * @param api EditText field containing the API
     */

    private void setAPI(EditText api) {
        final SharedPreferences.Editor edit = getSharedPreferences("apiKey", MODE_PRIVATE).edit();
        edit.putString("key", String.valueOf(api.getText()));
        edit.commit();
    }

    /**
     * If the API key is empty, it will set the key to the default school key.
     *
     * @param api EditText field containing the API
     */
    private void checkEmptyAPI(EditText api) {

        if (String.valueOf(api.getText()).contentEquals("")) {
            api.setText(DEFAULT_API_KEY);
            setAPI(api);
        }
    }
}