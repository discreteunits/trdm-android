package com.tastingroomdelmar.tastingroomdelmar;

import android.app.Application;

import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import com.facebook.FacebookSdk;

public class ParseIntegration extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this); /* Parse method enableLocalDatastore() must be called before initialize() */
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(this.getApplicationContext());

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
