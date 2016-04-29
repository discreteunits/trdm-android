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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier4ListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier4TopListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ItemListObject;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.TopListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;

public class Tier4Activity extends AppCompatActivity {
    private static final String TAG = Tier4Activity.class.getSimpleName();

    DrawerLayout drawer;

    ListView drawerListView;

    ArrayList<TopListObject> topListItem = new ArrayList<>();
    ArrayList<ItemListObject> listItem = new ArrayList<>();
    Tier4TopListViewAdapter topAdapter;
    Tier4ListViewAdapter adapter;

    ProgressBar mProgressBar;
    TextView nothing;

    String currentActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier4);

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
            currentActivity = extras.getString("TIER4_DEST");
            String prevActivity = extras.getString("TIER4_ORIG");

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

        nothing = (TextView) findViewById(R.id.nothing_to_display);
        nothing.setTypeface(FontManager.nexa);
        nothing.setVisibility(View.GONE);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_tier4);
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
                    case 0: break; // Dinein
                    case 1: break; // Takeaway
                    case 2: break; // Events
                    case 3: Intent tabIntent = new Intent(Tier4Activity.this, MyTabActivity.class);
                        startActivity(tabIntent);
                        break;
                    case 4: Intent paymentIntent = new Intent(Tier4Activity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break; // Payment
                    case 5: break; // Settings
                }
            }
        });

        final TwoWayView topListView = (TwoWayView) findViewById(R.id.lv_top_tier4);
        topAdapter = new Tier4TopListViewAdapter(this, topListItem);
        topListView.setAdapter(topAdapter); // TODO add license from https://github.com/lucasr/twoway-view
        topListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int count = adapterView.getAdapter().getCount();
                //Log.d(TAG, "adapterView size: " + count);

                selectCategory(position);
            }
        });

        getTopListFromParse();

        final ListView listView = (ListView) findViewById(R.id.lv_tier4);
        adapter = new Tier4ListViewAdapter(this,listItem);
        listView.setAdapter(adapter);

        getListFromParse();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            //Log.d(TAG, "position " + position + " clicked");
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

    private void getTopListFromParse() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Tier4");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        String name = objects.getString("name");
                        JSONArray tier3JSONArray = objects.getJSONArray("parentTiers"); // reds, whites,...

                        //Log.d(TAG, "parse object name : " + name);
                        try {
                            if (tier3JSONArray != null) {
                                for (int i = 0; i < tier3JSONArray.length(); i++) {
                                    String objectId = tier3JSONArray.getJSONObject(i).getString("objectId");

                                    if (OIDManager.isInList(objectId))
                                        topListItem.add(new TopListObject(objects));
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (topListItem.size() > 0)
                        Collections.sort(topListItem);

                    TopListObject fullList = new TopListObject("Full List");
                    fullList.setSelected(true); // default selected

                    topListItem.add(0, fullList);

                    topAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getListFromParse() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Product");
        query.include("categories");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        //TODO update this once db is correctly configured
                        if (objects.getNumber("stockAmount").intValue() < 1) {
                            // skip
                            continue;
                        }
                        String name = objects.getString("name");
                        String productType = objects.getString("productType");

                        if (productType == null) { productType = ""; }

                        //Log.d(TAG, "parse object name : " + name);

                        boolean addToListFlag;
                        try {
                            JSONArray arr = objects.getJSONArray("categories");
                            //CategoryManager.printCategory();

                            ArrayList<String> categoryList = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {
                                categoryList.add(arr.getJSONObject(i).getString("objectId"));
                            }

                            addToListFlag = CategoryManager.isInList(categoryList) &&
                                    (productType.equals("CHOICE") || productType.equals("GROUP") ||
                                     currentActivity.equals("Events") || currentActivity.equals("Merch") ||
                                    currentActivity.equals("Harvest") || currentActivity.equals("More"));

                            //Log.d(TAG, "addToListFlag: " + addToListFlag);

                            final ItemListObject item = new ItemListObject(objects);

                            if (addToListFlag) {
                                ParseQuery<ParseObject> verietalQuery = ParseQuery.getQuery("Category");

                                ArrayList<String> topList = new ArrayList<>();
                                for (TopListObject obj : topListItem) {
                                    topList.add(obj.getName());
                                }

                                String verietalId = CategoryManager.findVerietalId(arr, topList);
                                //Log.d(TAG, "VerietalId found: " + verietalId);
                                verietalQuery.getInBackground(verietalId, new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            item.setVerietal(object.getString("name"));
                                        } else {
                                            e.printStackTrace(); // usually when there's no result
                                            item.setVerietal("Unknown");
                                        }

                                        listItem.add(item);

                                        Collections.sort(listItem);

                                        adapter.notifyDataSetChanged();
                                        mProgressBar.setVisibility(View.GONE);
                                        nothing.setVisibility(View.GONE);
                                    }
                                });
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (listItem.size() == 0) {
                        //Log.d(TAG, "Nothing to show");
                        adapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.GONE);

                        nothing.setVisibility(View.VISIBLE);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /* selects category from top, change colors, shows filtered list */
    private void selectCategory(int position) {
        for (TopListObject obj : topListItem) {
            obj.setSelected(false);
        }

        topListItem.get(position).setSelected(true);
        topAdapter.notifyDataSetChanged();

        adapter.getFilter().filter(topListItem.get(position).getName());
    }


    public static class ItemViewHolder {
        public TextView tvName;
        public TextView tvInfo;
        public TextView tvVerietal;
        public TextView tvOption;
        public Button btnAddToTab;
    }
}