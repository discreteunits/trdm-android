package discreteunits.com.tastingroomdelmar.parseUtils;

import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import discreteunits.com.tastingroomdelmar.R;

/**
 * Created by Sean on 2/7/16.
 */
public class ParseUtility {
    private Context mContext;

    public ParseUtility(Context context) {
        mContext = context;
    }

    public void init() {
        Parse.enableLocalDatastore(mContext); /* Parse method enableLocalDatastore() must be called before initialize() */
        Parse.initialize(mContext, mContext.getString(R.string.parse_app_id), mContext.getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(mContext);

        FacebookSdk.sdkInitialize(mContext);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
