package discreteunits.com.tastingroomdelmar.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseFacebookUtils;

import discreteunits.com.tastingroomdelmar.R;
import io.fabric.sdk.android.Fabric;
import discreteunits.com.tastingroomdelmar.parseUtils.ParseUtility;
import discreteunits.com.tastingroomdelmar.utils.Constants;
import discreteunits.com.tastingroomdelmar.utils.FontManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static ParseUtility parseUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (parseUtility == null) {
            parseUtility = new ParseUtility(this);
            parseUtility.init();
        }

        setContentView(R.layout.activity_login);

        new FontManager(this);

        final Button mBtnSignup = (Button) findViewById(R.id.button_signup);
        mBtnSignup.setTypeface(FontManager.nexa);
        final Button mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnLogin.setTypeface(FontManager.nexa);
        final Button mBtnGuest = (Button) findViewById(R.id.button_guest_continue);
        mBtnGuest.setTypeface(FontManager.nexa);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                intent.putExtra("LOGIN_OR_SIGNUP", Constants.LOGIN_FLAG);
                startActivity(intent);
            }
        });

        mBtnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

//        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException err) {
//                if (user == null) {
//                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
//                } else if (user.isNew()) {
//                    Log.d(TAG, "User signed up and logged in through Facebook!");
//                } else {
//                    Log.d(TAG, "User logged in through Facebook!");
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
