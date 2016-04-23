package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier1ListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;

public class Tier1Activity extends AppCompatActivity {
    private static final String TAG = Tier1Activity.class.getSimpleName();

    DrawerLayout drawer;
    ListView drawerListView;

    ArrayList<ListObject> listItem = new ArrayList<>();
    Tier1ListViewAdapter adapter;

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setVisibility(View.GONE);

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        mTVPreviousActivityName.setText("Del Mar");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);

        mTVCurrentActivityName.setText("TASTING ROOM");
        mTVCurrentActivityName.setTypeface(FontManager.bebasReg);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);

        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        drawerListView = (ListView) findViewById(R.id.right_drawer);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    Intent intent = new Intent(Tier1Activity.this, MyTabActivity.class);
                    startActivity(intent);
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_tier1);
        mProgressBar.setVisibility(View.VISIBLE);

        final ListView listView = (ListView) findViewById(R.id.lv_tier1);

        adapter = new Tier1ListViewAdapter(this, listItem);

        listView.setAdapter(adapter);

        getListFromParse();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Log.d(TAG, "position " + position + " clicked");

                OIDManager.addToList(listItem.get(position).getId());
                //OIDManager.printObjectId();

                CategoryManager.addToList(listItem.get(position).getCategoryId());
                //CategoryManager.printCategory();

                if (listItem.get(position).getName().equals("Dine In")) {
                    CategoryManager.setDinein(true);
                } else {
                    CategoryManager.setDinein(false);
                }

                Intent intent;
                if (!listItem.get(position).skipToTier4()) {
                    intent = new Intent(Tier1Activity.this, Tier2Activity.class);
                    intent.putExtra("TIER2_DEST", listItem.get(position).getName());
                } else {
                    intent = new Intent(Tier1Activity.this, Tier4Activity.class);

                    intent.putExtra("TIER4_DEST", listItem.get(position).getName());
                    intent.putExtra("TIER4_ORIG", "Del Mar");
                }

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        OIDManager.popFromList();
        CategoryManager.popFromList();
        super.onDestroy();
    }

    private void getListFromParse() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Tier1");
        query.include("category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        //Log.d(TAG, "parse object name : " + objects.getString("name"));
                        //Log.d(TAG, "objectId : " + objects.getObjectId());

                        ParseObject categoryObject = objects.getParseObject("category");

                        listItem.add(new ListObject(
                                objects.getInt("sortOrder"),
                                objects.getObjectId(),
                                categoryObject == null ? "" : categoryObject.getObjectId(),
                                objects.getString("name"),
                                objects.getBoolean("skipToTier4")));

                        CategoryManager.addToAllList(categoryObject == null ? "" : categoryObject.getObjectId());
                    }

                    Collections.sort(listItem);
                    adapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}