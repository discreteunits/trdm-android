package com.tastingroomdelmar.tastingroomdelmar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Tier4_Reds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier4_reds);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_back); */

        TextView up_nav = (TextView) findViewById(R.id.up_nav_textView);
        up_nav.setText(R.string.title_activity_tier3__vines);
        /* An alternative to getPrevActivityName() may be getParentActivityName()
        TextView up_nav = (TextView) findViewById(R.id.up_nav_textView);
        up_nav.setText(PrevActivity.getPrevActivityName()); */

        TextView toolbar_header = (TextView) findViewById(R.id.toolbar_header);
        toolbar_header.setText(R.string.title_activity_tier4__reds);
    }

    public void goToParentActivity(View view) {
        Intent parentActivityIntent = new Intent(Tier4_Reds.this, Tier3_Vines.class);
        startActivity(parentActivityIntent);
    }

}