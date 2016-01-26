package com.tastingroomdelmar.tastingroomdelmar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

public class SignupLoginScene extends AppCompatActivity {

    EditText et_email;
    String email;
    EditText et_password;
    String password;
    Button signupButton;

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_login_scene);
    }

    public void createNewUser(View view) {
        et_email = (EditText) findViewById(R.id.userEmail);
        email = et_email.getText().toString();

        et_password = (EditText) findViewById(R.id.userPassword);
        password = et_password.getText().toString();

        signupButton = (Button) findViewById(R.id.signUpButton);

        user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Thanks! Signing in now", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupLoginScene.this, Tier1.class));
                } else {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}