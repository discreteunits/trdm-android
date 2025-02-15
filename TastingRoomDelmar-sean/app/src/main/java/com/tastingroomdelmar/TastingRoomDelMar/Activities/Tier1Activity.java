package com.tastingroomdelmar.TastingRoomDelMar.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters.Tier1ListViewAdapter;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ListObject;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ParseUtility;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

public class Tier1Activity extends AppCompatActivity {
    private static final String TAG = Tier1Activity.class.getSimpleName();

    DrawerLayout drawer;
    ListView drawerListView;

    ArrayList<ListObject> listItem = new ArrayList<>();
    Tier1ListViewAdapter adapter;

    ProgressBar mProgressBar;

    TextView mBadge;

    HashMap<String, String> cardObject;

    Dialog alertDialog;
    TextView alertTitle;
    TextView alertMsg;
    Button alertBtn;

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
        setContentView(R.layout.activity_tier1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        if (ParseUser.getCurrentUser() != null) {
            fetchCreditCard();

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!= null) {
                        Log.d(TAG, "ERRORROROROROROROROROROR\n"+e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            });

            ParsePush.subscribeInBackground("customer");
        }

        final ImageView mIVUp = (ImageView) findViewById(R.id.up_button);
        mIVUp.setVisibility(View.GONE);

        final TextView mTVPreviousActivityName = (TextView) findViewById(R.id.tv_prev_activity);
        final TextView mTVCurrentActivityName = (TextView) findViewById(R.id.tv_curr_activity);
        final ImageView mIVLogo = (ImageView) findViewById(R.id.primary_logo);
        mIVLogo.setVisibility(View.VISIBLE);
        mTVCurrentActivityName.setVisibility(View.GONE);

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

        mTVPreviousActivityName.setText("Del Mar");
        mTVPreviousActivityName.setTypeface(FontManager.nexa);

        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.layout_general_alert);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertTitle = (TextView) alertDialog.findViewById(R.id.tv_general_title);
        alertMsg = (TextView) alertDialog.findViewById(R.id.tv_general_msg);
        alertBtn = (Button) alertDialog.findViewById(R.id.btn_general_ok);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ImageButton mImageButtonDrawer = (ImageButton) findViewById(R.id.nav_button);

        final ImageButton mImageButtonTab = (ImageButton) findViewById(R.id.current_order);

        mImageButtonTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tabIntent = new Intent(Tier1Activity.this, MyTabActivity.class);
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
                    try {
                        switch (index) {
                            case 0:
                                Intent dineinIntent = new Intent(Tier1Activity.this, Tier2Activity.class);
                                CategoryManager.setDinein(true);
                                OIDManager.addToList(listItem.get(index).getId());
                                CategoryManager.addToList(listItem.get(index).getCategoryId(), listItem.get(index).getName());
                                CategoryManager.addToObjectList(listItem.get(index).getObject());
                                dineinIntent.putExtra("TIER2_DEST", "Dine In");
                                startActivity(dineinIntent);
                                break; // Dinein
                            case 1:
                                Intent takeawayintent = new Intent(Tier1Activity.this, Tier2Activity.class);
                                CategoryManager.setDinein(false);
                                OIDManager.addToList(listItem.get(index).getId());
                                CategoryManager.addToList(listItem.get(index).getCategoryId(), listItem.get(index).getName());
                                CategoryManager.addToObjectList(listItem.get(index).getObject());
                                takeawayintent.putExtra("TIER2_DEST", "Take Away");
                                startActivity(takeawayintent);
                                break; // Takeaway
                            case 2:
                                Intent eventIntent = new Intent(Tier1Activity.this, Tier4Activity.class);
                                CategoryManager.setDinein(false);
                                OIDManager.addToList(listItem.get(index).getId());
                                CategoryManager.addToList(listItem.get(index).getCategoryId(), listItem.get(index).getName());
                                CategoryManager.addToObjectList(listItem.get(index).getObject());
                                eventIntent.putExtra("TIER4_DEST", "Events");
                                eventIntent.putExtra("TIER4_ORIG", "Del Mar");
                                startActivity(eventIntent);
                                break; // Events
                            case 3:
                                Intent tabIntent = new Intent(Tier1Activity.this, MyTabActivity.class);
                                startActivity(tabIntent);
                                break;
                            case 4:
                                Intent paymentIntent = new Intent(Tier1Activity.this, PaymentActivity.class);
                                startActivity(paymentIntent);
                                break; // Payment
                            case 5:
                                Intent settingsIntent = new Intent(Tier1Activity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                break; // Settings
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        alertMsg.setText("Please wait until loading is done.");
                        alertDialog.show();
                    }
                }
            });
        }

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

                CategoryManager.addToList(listItem.get(position).getCategoryId(), listItem.get(position).getName());
                CategoryManager.addToObjectList(listItem.get(position).getObject());
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

//    @Override
//    protected void onDestroy() {
//        OIDManager.popFromList();
//        CategoryManager.popFromList();
//        super.onDestroy();
//    }

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
                        String state = "";
                        try {
                            state = categoryObject.fetchIfNeeded().getString("state");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        if (state.equals("idle")) continue;

                        listItem.add(new ListObject(
                                objects.getParseObject("category"),
                                objects.getInt("sortOrder"),
                                objects.getObjectId(),
                                categoryObject == null ? "" : categoryObject.getObjectId(),
                                objects.getString("name"),
                                objects.getBoolean("skipToTier4")));

                        CategoryManager.addToAllList(categoryObject == null ? "" : categoryObject.getObjectId());
                    }

                    Collections.sort(listItem);
                    OIDManager.setOIDs(listItem.get(0).getId(),listItem.get(1).getId(),listItem.get(2).getId());
                    CategoryManager.setCategories(
                            listItem.get(0).getObject(), listItem.get(0).getCategoryId(),
                            listItem.get(1).getObject(), listItem.get(1).getCategoryId(),
                            listItem.get(2).getObject(), listItem.get(2).getCategoryId()
                    );
                    adapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "ERRORROROROROROROROROROR");
                    e.printStackTrace();
                }
            }
        });
    }

    public void fetchCreditCard() {
        HashMap<String, String> params = new HashMap<>();
        String userObjectId = ParseUser.getCurrentUser().getObjectId();
        params.put("userId", userObjectId);
        ParseCloud.callFunctionInBackground("fetchCardForStripeCustomer", params, new FunctionCallback<HashMap<String, String>>() {
            @Override
            public void done(HashMap<String, String> object, ParseException e) {
                if (e == null) {
                    Log.d(TAG, object.get("brand"));
                    cardObject = object;
                    PaymentManager.getSingleton().setPaymentMethod(object);
                    ParseUtility.getSingleton().setHasCreditcard(true);
                } else {
                    e.printStackTrace();
                    ParseUtility.getSingleton().setHasCreditcard(false);
                }
            }
        });
    }
}