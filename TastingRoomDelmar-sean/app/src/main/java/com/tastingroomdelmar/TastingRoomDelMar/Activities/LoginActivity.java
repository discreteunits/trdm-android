package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;

import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import io.fabric.sdk.android.Fabric;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ParseUtility;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static ParseUtility parseUtility;

    AppCompatActivity appCompatActivity;

    Button mBtnSignup;
    Button mBtnLogin;
    Button mFBLoginButton;

    String origin;

    Context mContext;

    Dialog loadingDialog;

    Dialog alertDialog;
    TextView alertTitle;
    TextView alertMsg;
    Button alertBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (parseUtility == null) {
            parseUtility = new ParseUtility(this);
            parseUtility.init();
        }

        setContentView(R.layout.activity_login);

        mContext = this;
        appCompatActivity = this;

        final Intent currentIntent = getIntent();
        origin = currentIntent.getStringExtra("ORIGIN");

        new FontManager(getApplicationContext());

        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.layout_general_alert);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertTitle = (TextView) alertDialog.findViewById(R.id.tv_general_title);
        alertMsg = (TextView) alertDialog.findViewById(R.id.tv_general_msg);
        alertBtn = (Button) alertDialog.findViewById(R.id.btn_general_ok);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();

            final String userEmail = user.getEmail();
            edit.putString("firstname", user.getString("firstName"));
            edit.putString("lastname", user.getString("lastName"));
            edit.putString("mobile", user.getString("mobileNumber"));
            edit.putString("email", userEmail);
            edit.putBoolean("push", user.getBoolean("pushAllowed"));
            edit.putBoolean("newsletter", user.getBoolean("marketingAllowed"));
            edit.apply();

            if (userEmail == null || userEmail.isEmpty()) {
                alertTitle.setText("Whoops!");
                alertMsg.setText("We could not locate your email. Please enter an email.");
                alertDialog.show();

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                        intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                        intent.putExtra("ORIGIN", "email");
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                startActivity(intent);
            }
        }

        mBtnSignup = (Button) findViewById(R.id.button_signup);
        mBtnSignup.setTypeface(FontManager.nexa);
        mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnLogin.setTypeface(FontManager.nexa);
        final Button mBtnGuest = (Button) findViewById(R.id.button_guest_continue);
        mBtnGuest.setTypeface(FontManager.nexa);
        mFBLoginButton = (Button) findViewById(R.id.fb_login_button);
        mFBLoginButton.setTypeface(FontManager.nexa);

        loadingDialog = new Dialog(mContext);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.layout_loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        final TextView tvPleaseWait = (TextView) loadingDialog.findViewById(R.id.tv_please_wait);
        tvPleaseWait.setTypeface(FontManager.nexa);

        mFBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                if (ParseUser.getCurrentUser() == null) {
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(appCompatActivity, null, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (err != null) {
                                alertTitle.setText("Error");
                                alertMsg.setText("There was an error. Error Code["+err.getCode()+"]");
                                alertDialog.show();
                                alertDialog.setOnDismissListener(null);

                                Crashlytics.log("LoginActivity.ParseFacebookUtils.logInWithReadPermissionsInBackground:" + err.getLocalizedMessage() + "["+err.getCode()+"]");
                                loadingDialog.dismiss();
                                return;
                            }
                            if (user == null) {
                                Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Oops, There was an error!", Toast.LENGTH_SHORT).show();
                            } else if (user.isNew()) {
                                Log.d(TAG, "User signed up and logged in through Facebook!");
                                getAndSaveUserDetailsFromFB();
                            } else {
                                Log.d(TAG, "User logged in through Facebook!");

                                SharedPreferences prefs = PreferenceManager
                                        .getDefaultSharedPreferences(mContext);
                                SharedPreferences.Editor edit = prefs.edit();

                                final String userEmail = user.getEmail();

                                edit.putString("firstname", user.getString("firstName"));
                                edit.putString("lastname", user.getString("lastName"));
                                edit.putString("mobile", user.getString("mobileNumber"));
                                edit.putString("email", userEmail);
                                edit.putBoolean("push", user.getBoolean("pushAllowed"));
                                edit.putBoolean("newsletter", user.getBoolean("marketingAllowed"));
                                edit.apply();

                                loadingDialog.dismiss();

                                if (origin == null) {
                                    if (userEmail == null || userEmail.isEmpty()) {
                                        Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                                        intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                                        intent.putExtra("ORIGIN", "email");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (userEmail == null || userEmail.isEmpty()) {
                                        Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                                        intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                                        intent.putExtra("ORIGIN", origin + "+email");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MyTabActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });
                } else {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                OrderManager.clearOrders();
                                OIDManager.popAll();
                                CategoryManager.popAll();
                                PaymentManager.getSingleton().clearPaymentMethod();
                                PreferenceManager.getDefaultSharedPreferences(mContext).edit().clear().apply();

                                Toast.makeText(LoginActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                mBtnLogin.setVisibility(View.VISIBLE);
                                mBtnSignup.setVisibility(View.VISIBLE);
                                mFBLoginButton.setText("Login with Facebook");
                                mFBLoginButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fb_blue_soft_corner_button));
                            }

                            loadingDialog.dismiss();
                        }
                    });
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

    private String email = "";
    private void getAndSaveUserDetailsFromFB() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            final ParseUser user = ParseUser.getCurrentUser();

                            final String id = response.getJSONObject().getString("id");
                            Log.d(TAG, "id: " + id);


                            final String name = response.getJSONObject().getString("name");
                            Log.d(TAG, "name: " + name);

                            user.put("firstName", name.split(" ")[0]);
                            user.put("lastName", name.split(" ")[name.split(" ").length - 1]);

                            if (response.getJSONObject().length() > 2) {
                                final String fetchedEmail = response.getJSONObject().getString("email");
                                Log.d(TAG, "email: " + fetchedEmail);
                                user.setUsername(id);
                                user.setEmail(fetchedEmail);
                                email = fetchedEmail;
                            }

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        SharedPreferences prefs = PreferenceManager
                                                .getDefaultSharedPreferences(mContext);
                                        SharedPreferences.Editor edit = prefs.edit();

                                        edit.putString("firstname", user.getString("firstName"));
                                        edit.putString("lastname", user.getString("lastName"));
                                        edit.putString("mobile", user.getString("mobileNumber"));
                                        edit.putString("email", user.getEmail());
                                        edit.putBoolean("push", user.getBoolean("pushAllowed"));
                                        edit.putBoolean("newsletter", user.getBoolean("marketingAllowed"));
                                        edit.apply();

                                        loadingDialog.dismiss();

                                        if (origin == null) {
                                            if (email.equals("")) {
                                                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                                                intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                                                intent.putExtra("ORIGIN", "email");
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, Tier1Activity.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            if (email.equals("")) {
                                                Intent intent = new Intent(LoginActivity.this, SignUpLoginActivity.class);
                                                intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                                                intent.putExtra("ORIGIN", origin + "+email");
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MyTabActivity.class);
                                                startActivity(intent);
                                            }
                                        }

                                    } else {
                                        loadingDialog.dismiss();
                                        Crashlytics.log("LoginActivity.getAndSaveUserDetailsFromFB().user.saveInBackground:" + e.getLocalizedMessage() + "["+e.getCode()+"]");

                                        alertTitle.setText("Error");
                                        alertMsg.setText("There was an error. Error Code["+e.getCode()+"]");
                                        alertDialog.show();
                                        alertDialog.setOnDismissListener(null);

                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), e.getMessage().replace("java.lang.IllegalArgumentException: ", ""), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            loadingDialog.dismiss();
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
            mBtnLogin.setVisibility(View.INVISIBLE);
            mBtnSignup.setVisibility(View.INVISIBLE);
            mFBLoginButton.setText("Logout");

            if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser()))
                mFBLoginButton.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_soft_corner_button));
            else
                mFBLoginButton.setBackground(ContextCompat.getDrawable(this, R.drawable.fb_blue_soft_corner_button));

        } else {
            mBtnLogin.setVisibility(View.VISIBLE);
            mBtnSignup.setVisibility(View.VISIBLE);
            mFBLoginButton.setText("Login with Facebook");
            mFBLoginButton.setBackground(ContextCompat.getDrawable(this, R.drawable.fb_blue_soft_corner_button));
        }
    }
}
