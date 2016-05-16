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
import android.widget.LinearLayout;
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
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier2ListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;

public class Tier2Activity extends AppCompatActivity {
    private static final String TAG = Tier2Activity.class.getSimpleName();

    DrawerLayout drawer;
    ListView drawerListView;

    ArrayList<ListObject> listItem = new ArrayList<>();
    Tier2ListViewAdapter adapter;

    ProgressBar mProgressBar;

    String currentActivity = "";

    TextView mBadge;

    @Override
    protected void onResume() {
        if (mBadge != null) {
            if (OrderManager.getSingleton().getOrderCount() == 0)
                mBadge.setVisibility(View.GONE);
            else
                mBadge.setText(OrderManager.getSingleton().getOrderCount() + "");
        }

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier2);

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
                //finish();
                onBackPressed();
            }
        });

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);
        mBadge = (TextView) findViewById(R.id.tab_badge);

        OrderManager.getSingleton().setOrderCountListener(new OrderManager.OrderCountListener() {
            @Override
            public void onOrderCountChanged(int count) {
                if (count == 0)
                    mBadge.setVisibility(View.GONE);
                else {
                    mBadge.setVisibility(View.VISIBLE);
                    mBadge.setText(count + "");
                }
            }
        });

        if (FontManager.getSingleton() == null) new FontManager(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentActivity = extras.getString("TIER2_DEST");

            mTVCurrentActivityName.setText(currentActivity);
            mTVCurrentActivityName.setTypeface(FontManager.nexa);
        }

        mTVPreviousActivityName.setText("Del Mar");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);
        mTVPreviousActivityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                onBackPressed();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);

        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tabIntent = new Intent(Tier2Activity.this, MyTabActivity.class);
                startActivity(tabIntent);
            }
        });

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

        final LinearLayout drawerLayout = (LinearLayout) findViewById(R.id.drawer_view);

        for (int i = 0; i < drawerLayout.getChildCount()-1; i++) {
            final int index = i;
            final TextView tvItem = (TextView) drawerLayout.getChildAt(i);

            tvItem.setTypeface(FontManager.nexa);

            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index) {
                        case 0: Intent dineinIntent = new Intent(Tier2Activity.this, Tier2Activity.class);
                            CategoryManager.setDinein(true);
                            OIDManager.popFromList();
                            CategoryManager.popFromList();
                            dineinIntent.putExtra("TIER2_DEST", "Dine In");
                            startActivity(dineinIntent);
                            finish();
                            break; // Dinein
                        case 1: Intent takeawayintent = new Intent(Tier2Activity.this, Tier2Activity.class);
                            CategoryManager.setDinein(false);
                            OIDManager.popFromList();
                            CategoryManager.popFromList();
                            takeawayintent.putExtra("TIER2_DEST", "Take Away");
                            startActivity(takeawayintent);
                            finish();
                            break; // Takeaway
                        case 2: Intent eventIntent = new Intent(Tier2Activity.this, Tier4Activity.class);
                            CategoryManager.setDinein(false);
                            OIDManager.popFromList();
                            CategoryManager.popFromList();
                            eventIntent.putExtra("TIER4_DEST", "Events");
                            eventIntent.putExtra("TIER4_ORIG", currentActivity);
                            startActivity(eventIntent);
                            finish();
                            break; // Events
                        case 3: Intent tabIntent = new Intent(Tier2Activity.this, MyTabActivity.class);
                            startActivity(tabIntent);
                            break;
                        case 4: Intent paymentIntent = new Intent(Tier2Activity.this, PaymentActivity.class);
                            startActivity(paymentIntent);
                            break; // Payment
                        case 5: Intent settingsIntent = new Intent(Tier2Activity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                            break; // Settings
                    }
                }
            });
        }

        mProgressBar = (ProgressBar) findViewById(R.id.pb_tier2);
        mProgressBar.setVisibility(View.VISIBLE);

        final ListView listView = (ListView) findViewById(R.id.lv_tier2);

        adapter = new Tier2ListViewAdapter(this,listItem);

        listView.setAdapter(adapter);

        getListFromParse();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Log.d(TAG, "position " + position + " clicked");

                OIDManager.addToList(listItem.get(position).getId());
                //OIDManager.printObjectId();

                CategoryManager.addToList(listItem.get(position).getCategoryId(), listItem.get(position).getName());
                CategoryManager.addToObjectList(listItem.get(position).getObject());
                //CategoryManager.printCategory();

                Intent intent;
                if (!listItem.get(position).skipToTier4()) {
                    intent = new Intent(Tier2Activity.this, Tier3Activity.class);
                    intent.putExtra("TIER3_DEST", listItem.get(position).getName());
                    intent.putExtra("TIER3_ORIG", currentActivity);
                } else {
                    intent = new Intent(Tier2Activity.this, Tier4Activity.class);

                    intent.putExtra("TIER4_DEST", listItem.get(position).getName());
                    intent.putExtra("TIER4_ORIG", "Del Mar");
                }

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
            return;
        }

        OIDManager.popFromList();
        CategoryManager.popFromList();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getListFromParse() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tier2");
        query.include("category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        //Log.d(TAG, "parse object name : " + objects.getString("name"));
                        //Log.d(TAG, "objectId : " + objects.getObjectId());
                        ParseObject categoryObject = objects.getParseObject("category");
                        String state = "";
                        try {
                            state = categoryObject.fetchIfNeeded().getString("state");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        if (state.equals("idle")) continue;

                        try {
                            JSONArray arr = objects.getJSONArray("parentTiers");
                            CategoryManager.addToAllList(categoryObject == null ? "" : categoryObject.getObjectId());

                            for (int i = 0; i < arr.length(); i++) {
                                if (OIDManager.isInList(arr.getJSONObject(i).getString("objectId"))) {
                                    listItem.add(new ListObject(objects.getParseObject("category"), objects.getInt("sortOrder"),
                                            objects.getObjectId(),
                                            categoryObject == null ?
                                                    "" : categoryObject.getObjectId(),
                                            objects.getString("name"),
                                            objects.getBoolean("skipToTier4")));
                                }
                            }
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