package com.owen.cst2355finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String NEW_USER_TOAST_MESSAGE = "You haven't used the app yet.  " +
            "Entering your email and password will setup the app with your personal information";
    private SharedPreferences saveLogin = null;
    private SharedPreferences passFile = null;
    private LoginCredentials login;
    ApplicationDAO dao;

    /**
     * when the Activity is created it checks if this is the first time using the app and gives a toast message
     * it then gets the shared preferences for the email and password, and sets the buttons and input fields.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getSharedPreferences(Constants.LOGIN_SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .getString(Constants.LOGIN_EMAIL_STRING, "")
                .contentEquals(""))) {
            Toast.makeText(this, NEW_USER_TOAST_MESSAGE, Toast.LENGTH_LONG).show();
        }
        dao = new ApplicationDAO(this);
        saveLogin = getSharedPreferences(Constants.LOGIN_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        passFile = getSharedPreferences(Constants.PASS_FILE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        loadSharedPrefs();
        final EditText inputEmail = findViewById(R.id.enterEmail);
        final EditText inputPass = findViewById(R.id.enterPassword);
        final Button helpButton = findViewById(R.id.helpLoginButton);

        helpButton.setOnClickListener((click) -> {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.helpAlert)
                    .setMessage(getString(R.string.helpLogin))
                    .setPositiveButton(R.string.ok, (clicker, arg) -> {
                    })
                    .create()
                    .show();

        });

        if (savedInstanceState == null) {
            inputEmail.setText(login.getEmail());
            inputPass.setText(login.getPass());
        }
        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((click) -> authenticate(inputEmail, inputPass));
    }

    /**
     * Authenticate will take the login information and test the password then
     * set the login Object.
     *
     * @param email the users email
     * @param pass  the users password
     */

    private void authenticate(EditText email, EditText pass) {
        final Intent dashboard = new Intent(this, Dashboard.class);
        final String lastLogin = saveLogin.getString(Constants.LAST_LOGIN_STRING, null);
        dashboard.putExtra(Constants.LAST_LOGIN_STRING, lastLogin);

        if (passFile.getString(Constants.PASSWORD_STRING, "").contentEquals("")) {
            setNewPassword(pass);
            setPrefs(email, pass);
            startActivity(dashboard);
        } else if (!pass.getText().toString().contentEquals(passFile.getString(Constants.PASSWORD_STRING, ""))) {
            Toast.makeText(this, Constants.INCORRECT_PASSWORD_STRING, Toast.LENGTH_LONG).show();
        } else {
            setPrefs(email, pass);
            startActivity(dashboard);
        }
    }

    /**
     * this method saves the SharedPrefs to the phone.  It takes a LoginCredentials
     * object and determines if the password is to be saved or not.  This will also set
     * the lastLogin key to enable the information in the dashboard for Last Login.
     *
     * @param log an object of LoginCredentials which contains the present credentials
     */

    private void saveSharedPrefs(LoginCredentials log) {
        final CheckBox savePass = findViewById(R.id.passwordCheckbox);
        final SharedPreferences.Editor edit = saveLogin.edit();

        if (savePass.isChecked()) {
            edit.putString(Constants.LOGIN_EMAIL_STRING, log.getEmail());
            edit.putString(Constants.LOGIN_PASS_STRING, log.getPass());
        } else {
            edit.putString(Constants.LOGIN_EMAIL_STRING, log.getEmail());
            edit.putString(Constants.LOGIN_PASS_STRING, "");
        }
        edit.putString(Constants.LAST_LOGIN_STRING, new Date().toString());
        edit.commit();
    }

    /**
     * takes two EditTexts for the email and password and sets the
     * LoginCredentials object, then saves the SharedPrefs.
     *
     * @param email user's email
     * @param pass  user's password
     */

    private void setPrefs(EditText email, EditText pass) {
        login.setEmail(email.getText().toString());
        login.setPass(pass.getText().toString());
        saveSharedPrefs(login);
    }

    /**
     * this method creates the sharedPref with the new password in it
     *
     * @param newPass user's new password for first time installing
     */

    private void setNewPassword(EditText newPass) {
        final SharedPreferences.Editor edit = passFile.edit();
        edit.putString(Constants.PASSWORD_STRING, newPass.getText().toString());
        edit.commit();
    }

    /**
     * this method gets the sharedPreference data from the file and
     * creates a new LoginCredentials Object.
     */

    private void loadSharedPrefs() {
        this.login = new LoginCredentials(
                saveLogin.getString(Constants.LOGIN_EMAIL_STRING, ""),
                saveLogin.getString(Constants.LOGIN_PASS_STRING, ""));
    }

    /**
     * private class for the login page that holds the login credentials
     */
    private class LoginCredentials {

        private String email;
        private String pass;

        protected LoginCredentials(String email) {
            this(email, "");
        }

        protected LoginCredentials(String email, String pass) {
            this.email = email;
            this.pass = pass;
        }

        protected String getEmail() {
            return this.email;
        }

        protected String getPass() {
            return this.pass;
        }

        protected void setEmail(String email) {
            this.email = email;
        }

        protected void setPass(String pass) {
            this.pass = pass;
        }
    }
}