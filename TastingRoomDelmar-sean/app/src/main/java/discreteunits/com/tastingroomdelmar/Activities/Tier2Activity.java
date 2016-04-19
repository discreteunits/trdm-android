package discreteunits.com.tastingroomdelmar.Activities;

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

import discreteunits.com.tastingroomdelmar.R;
import discreteunits.com.tastingroomdelmar.ListViewAdapters.Tier2ListViewAdapter;
import discreteunits.com.tastingroomdelmar.parseUtils.ListObject;
import discreteunits.com.tastingroomdelmar.utils.CategoryManager;
import discreteunits.com.tastingroomdelmar.utils.FontManager;
import discreteunits.com.tastingroomdelmar.utils.OIDManager;

public class Tier2Activity extends AppCompatActivity {
    private static final String TAG = Tier2Activity.class.getSimpleName();

    DrawerLayout drawer;
    ArrayList<ListObject> listItem = new ArrayList<>();
    Tier2ListViewAdapter adapter;

    ProgressBar mProgressBar;

    String currentActivity = "";

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
                finish();
            }
        });

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

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
                finish();
            }
        });

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

                CategoryManager.addToList(listItem.get(position).getCategoryId());
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
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tier2");
        query.include("category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        //Log.d(TAG, "parse object name : " + objects.getString("name"));
                        //Log.d(TAG, "objectId : " + objects.getObjectId());
                        ParseObject categoryObject = objects.getParseObject("category");



                        try {
                            JSONArray arr = objects.getJSONArray("parentTiers");
                            CategoryManager.addToAllList(categoryObject == null ? "" : categoryObject.getObjectId());

                            for (int i = 0; i < arr.length(); i++) {
                                if (OIDManager.isInList(arr.getJSONObject(i).getString("objectId"))) {
                                    listItem.add(new ListObject(objects.getInt("sortOrder"),
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