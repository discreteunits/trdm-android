package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by kor_s on 4/20/2016.
 */
public class SignUpSecondActivity extends AppCompatActivity {
    Context mContext;

    Dialog alertDialog;
    TextView alertTitle;
    TextView alertMsg;
    Button alertBtn;

    @Override
    protected void onResume() {
        if (ParseUser.getCurrentUser() != null) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();

            edit.putString("firstname", ParseUser.getCurrentUser().getString("firstName"));
            edit.putString("lastname", ParseUser.getCurrentUser().getString("lastName"));
            edit.putString("mobile", ParseUser.getCurrentUser().getString("mobileNumber"));
            edit.putString("email", ParseUser.getCurrentUser().getEmail());
            edit.putBoolean("push", ParseUser.getCurrentUser().getBoolean("pushAllowed"));
            edit.putBoolean("newsletter", ParseUser.getCurrentUser().getBoolean("marketingAllowed"));
            edit.apply();

            Intent intent = new Intent(SignUpSecondActivity.this, Tier1Activity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

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

        mContext = this;

        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        final Button signupButton = (Button) findViewById(R.id.button_signup);
        final TextView firstnameLabel = (TextView) findViewById(R.id.tv_first_name);
        final EditText firstname = (EditText) findViewById(R.id.et_first_name);
        final TextView lastnameLabel = (TextView) findViewById(R.id.tv_last_name);
        final EditText lastname = (EditText) findViewById(R.id.et_last_name);
        final TextView mobileLabel = (TextView) findViewById(R.id.tv_mobile_num);
        final EditText mobile = (EditText) findViewById(R.id.et_mobile_num);
        final TextView newsletterLabel = (TextView) findViewById(R.id.tv_newsletter);
        final AppCompatCheckBox newsletter = (AppCompatCheckBox) findViewById(R.id.cb_newsletter);

        final TextView mTVPreviousActivityName = (TextView) toolbar.findViewById(R.id.tv_prev_activity);
        final TextView  mTVCurrentActivityName = (TextView) toolbar.findViewById(R.id.tv_curr_activity);

        mTVCurrentActivityName.setText(getResources().getString(R.string.signup));
        mTVPreviousActivityName.setText("Back");
        mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTVPreviousActivityName.setTypeface(FontManager.nexa);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        signupButton.setBackgroundColor(ContextCompat.getColor(this, R.color.confirmGreen));
        signupButton.setText(getResources().getString(R.string.signup));
        signupButton.setTypeface(FontManager.nexa);
        firstnameLabel.setTypeface(FontManager.bebasReg);
        firstname.setTypeface(FontManager.bebasReg);
        lastnameLabel.setTypeface(FontManager.bebasReg);
        lastname.setTypeface(FontManager.bebasReg);
        mobileLabel.setTypeface(FontManager.bebasReg);
        mobile.setTypeface(FontManager.bebasReg);
        newsletterLabel.setTypeface(FontManager.bebasReg);

        Intent i = getIntent();
        final String email = i.getStringExtra("email");
        final String password = i.getStringExtra("password");
        final String origin = i.getStringExtra("ORIGIN");

        alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.layout_general_alert);
        alertTitle = (TextView) alertDialog.findViewById(R.id.tv_general_title);
        alertMsg = (TextView) alertDialog.findViewById(R.id.tv_general_msg);
        alertBtn = (Button) alertDialog.findViewById(R.id.btn_general_ok);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (signupButton != null && firstname != null && lastname != null && mobile != null && newsletter != null) {
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstname.getText() == null || lastname.getText() == null || mobile.getText() == null ||
                        firstname.getText().length() == 0 || lastname.getText().length() == 0) {
                        Toast.makeText(mContext, "All fields are required!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String first = firstname.getText().toString();
                    final String last = lastname.getText().toString();
                    final String mobilenum = mobile.getText().toString();
                    final boolean subscribe = newsletter.isChecked();

                    final ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.put("firstName", first);
                    user.put("lastName", last);
                    user.put("mobileNumber", mobilenum);
                    user.put("pushAllowed", subscribe);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
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

                                Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();

                                if (origin == null) {
                                    startActivity(new Intent(SignUpSecondActivity.this, Tier1Activity.class));
                                } else {
                                    startActivity(new Intent(SignUpSecondActivity.this, MyTabActivity.class));
                                }
                            } else {
                                if (e.getCode() == ParseException.USERNAME_TAKEN)
                                    alertMsg.setText("This account already exists, try logging in.");
                                else
                                    alertMsg.setText("There was an error. Error Code["+ e.getCode() +"]");

                                alertDialog.show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }



    }
}
