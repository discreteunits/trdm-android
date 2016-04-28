package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;

import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.stripe.Stripe;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import io.fabric.sdk.android.Fabric;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ParseUtility;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static ParseUtility parseUtility;

    AppCompatActivity appCompatActivity;

    ImageButton mFBLoginButton;

    String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (parseUtility == null) {
            parseUtility = new ParseUtility(this);
            parseUtility.init();
        }

        setContentView(R.layout.activity_login);

        final Intent currentIntent = getIntent();
        origin = currentIntent.getStringExtra("ORIGIN");

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
            startActivity(intent);
        }

        appCompatActivity = this;

        new FontManager(getApplicationContext());

        final Button mBtnSignup = (Button) findViewById(R.id.button_signup);
        mBtnSignup.setTypeface(FontManager.nexa);
        final Button mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnLogin.setTypeface(FontManager.nexa);
        final Button mBtnGuest = (Button) findViewById(R.id.button_guest_continue);
        mBtnGuest.setTypeface(FontManager.nexa);
        mFBLoginButton = (ImageButton) findViewById(R.id.fb_login_button);

        mFBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ParseUser.getCurrentUser() == null) {
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(appCompatActivity, null, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (err != null) {
                                Toast.makeText(getApplicationContext(), err.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (user == null) {
                                Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                Toast.makeText(getApplicationContext(), "Oops, There was an error!", Toast.LENGTH_SHORT).show();
                            } else if (user.isNew()) {
                                Log.d(TAG, "User signed up and logged in through Facebook!");
                                getAndSaveUserDetailsFromFB();
                            } else {
                                Log.d(TAG, "User logged in through Facebook!");

                                if (origin == null) {
                                    Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MyTabActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                } else {
                    ParseUser.logOutInBackground();
                    Toast.makeText(LoginActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    mFBLoginButton.setImageDrawable(ContextCompat.getDrawable(appCompatActivity, R.drawable.login_with_fb));
                }
            }
        });

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                intent.putExtra("ORIGIN", origin);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                intent.putExtra("LOGIN_OR_SIGNUP", Constants.LOGIN_FLAG);
                intent.putExtra("ORIGIN", origin);
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
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void getAndSaveUserDetailsFromFB() {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            String id = response.getJSONObject().getString("id");
                            Log.d(TAG, "id: " + id);

                            String name = response.getJSONObject().getString("name");
                            Log.d(TAG, "name: "+ name);

                            ParseUser user = ParseUser.getCurrentUser();
                            user.setUsername(id);
                            user.put("firstName", name.split(" ")[0]);
                            user.put("lastName", name.split(" ")[name.split(" ").length - 1]);

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();

                                        if (origin == null) {
                                            Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, MyTabActivity.class);
                                            startActivity(intent);
                                        }

                                    } else {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), e.getMessage().replace("java.lang.IllegalArgumentException: ", ""), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ParseUser.getCurrentUser() != null) {
            mFBLoginButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logout_with_fb));
        }
    }
}
