package com.tastingroomdelmar.tastingroomdelmar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Intent;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import javax.security.auth.login.LoginException;

public class LogIn extends AppCompatActivity {

    private static final String TAG = "LogIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    /* ConnectivityManager for checking if device is connected to internet. Check after FB
    button is touched and before intent is sent to FB. */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Facebook returned with requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    startTier1();
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    startTier1();
                }

                if (err != null) {
                    err.printStackTrace();
                }
            }
        });
    }

    public void startSignUpLoginScene(View view) {
        PrevActivity.setPrevActivityName("Login");
        Intent intent = new Intent(LogIn.this, SignupLoginScene.class);
        startActivity(intent);
    }

    public void startTier1(View view) {
        Intent intent = new Intent(LogIn.this, Tier1.class);
        startActivity(intent);
    }

    public void startTier1() {
        Intent intent = new Intent(LogIn.this, Tier1.class);
        startActivity(intent);
    }
}