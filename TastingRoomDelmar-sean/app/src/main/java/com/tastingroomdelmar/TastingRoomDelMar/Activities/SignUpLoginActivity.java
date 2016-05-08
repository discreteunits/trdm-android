package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.SaveCallback;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

import java.util.List;

public class SignUpLoginActivity extends AppCompatActivity {
    private static final String TAG = SignUpLoginActivity.class.getSimpleName();

    TextView mTVPreviousActivityName;
    TextView mTVCurrentActivityName;
    TextView mTVQuestion;
    TextView mTVEmail;
    TextView mTVPassword;

    Button mButtonSignupLogin;

    EditText mEditTextEmail;
    EditText mEditTextPassword;

    String origin;

    Context mContext;

    @Override
    protected void onResume() {
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

            if (userEmail != null && userEmail.isEmpty()) {
                Intent intent = new Intent(SignUpLoginActivity.this, Tier1Activity.class);
                startActivity(intent);
            }
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_login);

        mContext = this;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setVisibility(View.VISIBLE);
        mIVUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);
        mImageButtonDrawer.setVisibility(View.GONE);

        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);
        mImageButtonTab.setVisibility(View.GONE);


        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        mTVPreviousActivityName = (TextView) toolbar.findViewById(R.id.tv_prev_activity);
        mTVPreviousActivityName.setTypeface(FontManager.nexa);

        mTVCurrentActivityName = (TextView) toolbar.findViewById(R.id.tv_curr_activity);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        mTVQuestion = (TextView) findViewById(R.id.tv_signup_login_question);
        mTVQuestion.setTypeface(FontManager.nexa);

        mButtonSignupLogin = (Button) findViewById(R.id.button_signup_login);
        mButtonSignupLogin.setTypeface(FontManager.nexa);

        mTVEmail = (TextView) findViewById(R.id.tv_email);
        mTVEmail.setTypeface(FontManager.bebasReg);
        mEditTextEmail = (EditText) findViewById(R.id.et_email);
        mEditTextEmail.setTypeface(FontManager.bebasReg);

        mTVPassword = (TextView) findViewById(R.id.tv_password);
        mTVPassword.setTypeface(FontManager.bebasReg);

        mEditTextPassword = (EditText) findViewById(R.id.et_password);

        final View thirdDivider = (View) findViewById(R.id.third_divider);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            origin = extras.getString("ORIGIN");
            int flagSignupOrLogin = extras.getInt("LOGIN_OR_SIGNUP");

            if (flagSignupOrLogin == Constants.SIGNUP_FLAG) {
                mTVCurrentActivityName.setText(getResources().getString(R.string.signup));
                mTVQuestion.setText(getResources().getString(R.string.question_signup));
                mTVQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SignUpLoginActivity.this, SignUpLoginActivity.class);
                        intent.putExtra("LOGIN_OR_SIGNUP", Constants.LOGIN_FLAG);
                        intent.putExtra("ORIGIN", origin);
                        startActivity(intent);
                        finish();
                    }
                });

                if (origin == null) {
                    mButtonSignupLogin.setText(getResources().getString(R.string.con));
                    mTVPassword.setVisibility(View.VISIBLE);
                    mEditTextPassword.setVisibility(View.VISIBLE);
                    thirdDivider.setVisibility(View.VISIBLE);
                    mTVQuestion.setVisibility(View.VISIBLE);
                } else {
                    mButtonSignupLogin.setText(getResources().getString(R.string.signup));
                    mTVPassword.setVisibility(View.INVISIBLE);
                    mEditTextPassword.setVisibility(View.INVISIBLE);
                    thirdDivider.setVisibility(View.INVISIBLE);
                    mTVQuestion.setVisibility(View.INVISIBLE);
                }

                mButtonSignupLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.confirmGreen));
                mButtonSignupLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        proceedCreateNewUser();
                    }
                });
            }
            else {
                mTVCurrentActivityName.setText(getResources().getString(R.string.login));
                mTVQuestion.setText(getResources().getString(R.string.question_login));
                mTVQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SignUpLoginActivity.this, SignUpLoginActivity.class);
                        intent.putExtra("LOGIN_OR_SIGNUP", Constants.SIGNUP_FLAG);
                        intent.putExtra("ORIGIN", origin);
                        startActivity(intent);
                        finish();
                    }
                });
                mButtonSignupLogin.setText(getResources().getString(R.string.login));
                mButtonSignupLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.grayText));
                mButtonSignupLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginUser();
                    }
                });
            }
        }

        mTVPreviousActivityName.setVisibility(View.GONE);
    }

    private void proceedCreateNewUser() {
        final String email = mEditTextEmail.getText().toString();

        final String password = mEditTextPassword.getText().toString();

        if (origin == null) {
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or Password cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ParseUser user = ParseUser.getCurrentUser();
        user.setEmail(email);
        user.setUsername(email);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("email", email);
                    edit.apply();

                    if (origin != null) {
                        if (origin.equals("MyTabActivity+email")) {
                            Toast.makeText(mContext, "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpLoginActivity.this, MyTabActivity.class));
                        } else if (origin.equals("email")) {
                            Toast.makeText(mContext, "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpLoginActivity.this, Tier1Activity.class));
                        }
                    } else {
                        Intent intent = new Intent(SignUpLoginActivity.this, SignUpSecondActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("ORIGIN", origin);
                        startActivity(intent);
                    }
                } else {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser() {
        final String email = mEditTextEmail.getText().toString();

        final String password = mEditTextPassword.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.saveInBackground();

                    ParsePush.subscribeInBackground("customer");

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

                    Toast.makeText(getApplicationContext(), "Thanks! Logging in now", Toast.LENGTH_SHORT).show();

                    if (origin == null) {
                        startActivity(new Intent(SignUpLoginActivity.this, Tier1Activity.class));
                    } else {
                        startActivity(new Intent(SignUpLoginActivity.this, MyTabActivity.class));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password is wrong", Toast.LENGTH_SHORT).show();
                }

                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }
}
