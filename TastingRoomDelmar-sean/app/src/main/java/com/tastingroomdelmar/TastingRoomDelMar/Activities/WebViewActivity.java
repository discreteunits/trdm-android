package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by kor_s on 4/30/2016.
 */
public class WebViewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String target = intent.getStringExtra("TARGET");

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setVisibility(View.VISIBLE);
        mIVUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                onBackPressed();
            }
        });

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        mTVPreviousActivityName.setText("Back");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);
        mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTVCurrentActivityName.setText(target);
        mTVCurrentActivityName.setTypeface(FontManager.nexa);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);
        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonDrawer.setVisibility(View.GONE);
        mImageButtonTab.setVisibility(View.GONE);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        myWebView.loadUrl("http://www.tastingroomdelmar.com/terms/");
    }
}
