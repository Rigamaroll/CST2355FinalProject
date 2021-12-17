package com.owen.cst2355finalproject;

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
    SharedPreferences saveLogin = null;
    SharedPreferences passFile = null;
    private LoginCredentials login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((getSharedPreferences("login", Context.MODE_PRIVATE).getString("loginEmail", "").contentEquals(""))) {
            Toast newUser = Toast.makeText(this, "You haven't used the app yet.  " +
                    "Entering your email and password will setup the app with your personal information", Toast.LENGTH_LONG);
            newUser.show();
        }

        saveLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        passFile = getSharedPreferences("passFile", Context.MODE_PRIVATE);
        loadSharedPrefs();
        EditText inputEmail = findViewById(R.id.enterEmail);
        EditText inputPass = findViewById(R.id.enterPassword);

        if (savedInstanceState == null) {

            inputEmail.setText(login.getEmail());
            inputPass.setText(login.getPass());

        }



        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((click) -> {

            authenticate(inputEmail, inputPass);

        });

    }

    /**
     * Authenticate will take the login information and test the password then
     * set the login Object.
     * @param email
     * @param pass
     */

    private void authenticate(EditText email, EditText pass) {

        Intent dashboard = new Intent(this, Dashboard.class);
        String lastLogin = saveLogin.getString("lastLogin", null);
        dashboard.putExtra("lastLogin", lastLogin);

        if (passFile.getString("password", "").contentEquals("")) {

            setNewPassword(pass);
            setPrefs(email, pass);
            startActivity(dashboard);

        } else if (!pass.getText().toString().contentEquals(passFile.getString("password", ""))) {

            Toast wrongPass = Toast.makeText(this, "Your password is incorrect.  Please try again!", Toast.LENGTH_LONG);
            wrongPass.show();

        } else {

            setPrefs(email, pass);
            startActivity(dashboard);
        }
    }

    /**
     * this method saves the SharedPrefs to the phone.  It takes a LoginCredentials
     * object and determines if the password is to be saved or not.
     *
     * @param log
     */

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
        edit.putString("lastLogin", new Date().toString());
        edit.commit();
    }

    /**
     * takes two EditTexts for the email and password and sets the
     * LoginCredentials object, then saves the SharedPrefs.
     *
     * @param email
     * @param pass
     */
    private void setPrefs(EditText email, EditText pass) {

        login.setEmail(email.getText().toString());
        login.setPass(pass.getText().toString());
        saveSharedPrefs(login);

    }

    /**
     * this method creates the sharedPref with the new password in it
     *
     * @param newPass
     */
    private void setNewPassword(EditText newPass) {

        SharedPreferences.Editor edit = passFile.edit();
        edit.putString("password", newPass.getText().toString());
        edit.commit();
    }

    /**
     * this method gets the sharedPreference data from the file and
     * creates a new LoginCredentials Object.
     */

    private void loadSharedPrefs() {

        this.login = new LoginCredentials(saveLogin.getString("loginEmail", ""), saveLogin.getString("loginPass", ""));

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