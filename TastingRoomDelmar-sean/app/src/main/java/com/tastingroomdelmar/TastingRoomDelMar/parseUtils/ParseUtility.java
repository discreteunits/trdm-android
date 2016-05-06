package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import com.parse.PushService;
import com.parse.SaveCallback;
import com.stripe.Stripe;
import com.tastingroomdelmar.TastingRoomDelMar.R;

/**
 * Created by Sean on 2/7/16.
 */
public class ParseUtility {
    private Context mContext;

    private boolean hasCreditcard;

    private static ParseUtility singleton;

    public ParseUtility(Context context) {
        mContext = context;
        singleton = this;
    }

    public static ParseUtility getSingleton() {
        return singleton;
    }

    public void init() {
        Parse.enableLocalDatastore(mContext); /* Parse method enableLocalDatastore() must be called before initialize() */
        Parse.initialize(mContext, mContext.getString(R.string.parse_app_id), mContext.getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(mContext);

        FacebookSdk.sdkInitialize(mContext);

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("ParseUtility", "ERRORROROROROROROROROROR");
                    e.printStackTrace();
                }
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());

            try {
                installation.save();
                ParsePush.subscribeInBackground("customer");
            } catch (ParseException e) {
                    Toast.makeText(mContext, "There was an error logging in. Please login again", Toast.LENGTH_SHORT).show();
                    ParseUser.logOut();
                    Log.d("ParseUtility", "ERRORROROROROROROROROROR");
                    e.printStackTrace();
            }
        }
    }

    public boolean getHasCreditCard() {
        return hasCreditcard;
    }

    public void setHasCreditcard(boolean b) {
        hasCreditcard = b;
    }
}
