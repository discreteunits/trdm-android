package discreteunits.com.tastingroomdelmar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import parseUtils.ListObject;
import utils.TagManager;

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

        final AssetManager assetManager = getAssets();
        final Typeface nexarust = Typeface.createFromAsset(assetManager, "fonts/nexarust/NexaRustScriptL-0.otf");

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentActivity = extras.getString("TIER2_DEST");

            mTVCurrentActivityName.setText(currentActivity);
            mTVCurrentActivityName.setTypeface(nexarust);
        }

        mTVPreviousActivityName.setText("Del Mar");
        mTVPreviousActivityName.setTypeface(nexarust);
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
                Log.d(TAG, "position " + position + " clicked");

                TagManager.addToList(listItem.get(position).getTag());
                TagManager.printTag();

                Intent intent = new Intent(Tier2Activity.this, Tier3Activity.class);
                intent.putExtra("TIER3_DEST", listItem.get(position).getName());
                intent.putExtra("TIER3_ORIG", currentActivity);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        TagManager.popFromList();
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
        query.include("tag");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    for(ParseObject objects : objectList) {
                        Log.d(TAG, "parse object name : " + objects.getString("name"));
                        Log.d(TAG, "tag name : " + objects.getObjectId());

                        listItem.add(new ListObject(objects.getInt("sortOrder"),
                                                    objects.getObjectId(),
                                                    objects.getString("name")));
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