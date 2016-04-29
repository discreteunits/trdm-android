package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier3ListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;

public class Tier3Activity extends AppCompatActivity {
    private static final String TAG = Tier3Activity.class.getSimpleName();

    DrawerLayout drawer;
    ListView drawerListView;

    ArrayList<ListObject> listItem = new ArrayList<>();
    Tier3ListViewAdapter adapter;

    ProgressBar mProgressBar;

    String currentActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier3);

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

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentActivity = extras.getString("TIER3_DEST");
            String prevActivity = extras.getString("TIER3_ORIG");

            mTVCurrentActivityName.setText(currentActivity);
            mTVCurrentActivityName.setTypeface(FontManager.nexa);

            mTVPreviousActivityName.setText(prevActivity);
            mTVPreviousActivityName.setTypeface(FontManager.nexa);
            mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        mProgressBar = (ProgressBar) findViewById(R.id.pb_tier3);
        mProgressBar.setVisibility(View.VISIBLE);

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
                switch (i) {
                    case 0: break; // Menu
                    case 1: break; // Events
                    case 2: Intent intent = new Intent(Tier3Activity.this, MyTabActivity.class);
                        startActivity(intent);
                        break;
                    case 3: break; // Payment
                    case 4: break; // Settings
                }
            }
        });

        final ListView listView = (ListView) findViewById(R.id.lv_tier3);


        adapter = new Tier3ListViewAdapter(this,listItem);

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

                Intent intent = new Intent(Tier3Activity.this, Tier4Activity.class);

                intent.putExtra("TIER4_DEST", listItem.get(position).getName());
                intent.putExtra("TIER4_ORIG", currentActivity);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getListFromParse() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tier3");
        query.include("category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        String name = objects.getString("name");
                        String objectId = objects.getObjectId();
                        int sortOrder = objects.getInt("sortOrder");
                        ParseObject categoryObject = objects.getParseObject("category");

                        //Log.d(TAG, "parse object name : " + name);
                        //Log.d(TAG, "ObjectId : " + objectId);
                        try {
                            JSONArray arr = objects.getJSONArray("parentTiers");

                            CategoryManager.addToAllList(categoryObject == null ?
                                    "" : categoryObject.getObjectId());

                            for (int i = 0; i < arr.length(); i++) {
                                if (OIDManager.isInList(arr.getJSONObject(i).getString("objectId"))) {
                                    listItem.add(new ListObject(
                                            sortOrder,
                                            objectId,
                                            categoryObject == null ?
                                                    "" : categoryObject.getObjectId(),
                                            name,
                                            false));

                                    break;
                                }
                            }
                            //if (OIDManager.isInList(objects.getJSONObject("tier2").getString("Tier2")))
                            //    listItem.add(new ListObject(sortOrder, objectId, name));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
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