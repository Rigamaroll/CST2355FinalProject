package com.owen.cst2355finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences saveLogin = null;
    SharedPreferences passFile = null;
    private LoginCredentials login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getSharedPreferences("login",Context.MODE_PRIVATE).getString("loginEmail","").contentEquals(""))) {
            Toast newUser = Toast.makeText(this, "You haven't used the app yet.  " +
                    "Entering your email and password will setup the app with your personal information",Toast.LENGTH_LONG);
            newUser.show();
        }

        saveLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        passFile = getSharedPreferences("passFile", Context.MODE_PRIVATE);
        loadSharedPrefs();

        EditText inputEmail = findViewById(R.id.enterEmail);
        inputEmail.setText(login.getEmail());
        EditText inputPass = findViewById(R.id.enterPassword);
        inputPass.setText(login.getPass());

        Intent dashboard = new Intent(this, Dashboard.class);
        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((click) -> {

            if (passFile.getString("password","").contentEquals("")) {

                setNewPassword(inputPass.getText().toString());
                login.setEmail(inputEmail.getText().toString());
                login.setPass(inputPass.getText().toString());
                saveSharedPrefs(login);
                startActivity(dashboard);

            } else if (!inputPass.getText().toString().contentEquals(passFile.getString("password", ""))) {

                Toast wrongPass = Toast.makeText(this, "Your password is incorrect.  Please try again!",Toast.LENGTH_LONG);
                wrongPass.show();

            } else {
                login.setEmail(inputEmail.getText().toString());
                login.setPass(inputPass.getText().toString());
                saveSharedPrefs(login);
                startActivity(dashboard);

            }

        });

    }
    //this method saves the information to the SharedPreferences file

    private void saveSharedPrefs(LoginCredentials log) {
        CheckBox savePass = findViewById(R.id.passwordCheckbox);
        SharedPreferences.Editor edit = saveLogin.edit();

        if (savePass.isChecked()) {

            edit.putString("loginEmail", log.getEmail());
            edit.putString("loginPass", log.getPass());

        } else {

            edit.putString("loginEmail", log.getEmail());
            edit.putString("loginPass", "");

        }

        edit.commit();
    }

    private void setNewPassword (String newPass) {

        SharedPreferences.Editor edit = passFile.edit();
        edit.putString("password", newPass);
        edit.commit();
    }

    /**
     *    this method gets the sharedPreference data from the file and
     *    creates a new LoginCredentials Object.
     */

    private void loadSharedPrefs() {

        this.login = new LoginCredentials(saveLogin.getString("loginEmail", ""), saveLogin.getString("loginPass", ""));

    }

    /**
     * private class for the login page that holds the login credentials
     */

    private class LoginCredentials {

        protected String email;
        protected String pass;

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