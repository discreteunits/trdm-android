package com.tastingroomdelmar.tastingroomdelmar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Tier2_DineIn extends AppCompatActivity {

    private ListView dineInListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier2_dine_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tier2_dine_in_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        dineInListView = (ListView) findViewById(R.id.tier2_listView);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                switch (position) {
                    case 0:
                        /* Toast.makeText(Tier2_DineIn.this, "Starts Vines activity", Toast.LENGTH_SHORT).show(); */
                        Intent vinesIntent = new Intent(Tier2_DineIn.this, Tier3_Vines.class);
                        startActivity(vinesIntent);
                        break;
                    case 1:
                        Toast.makeText(Tier2_DineIn.this, "Starts Hops activity", Toast.LENGTH_SHORT).show();
                        /* Intent hopsIntent = new Intent(Tier2_DineIn.this, Tier3_Hops.class);
                        startActivity(hopsIntent); */
                        break;
                    case 2:
                        Toast.makeText(Tier2_DineIn.this, "Starts Harvest activity", Toast.LENGTH_SHORT).show();
                        /* Intent harvestIntent = new Intent(Tier2_DineIn.this, Tier3_Harvest.class);
                        startActivity(harvestIntent); */
                        break;
                    case 3:
                        Toast.makeText(Tier2_DineIn.this, "Starts More activity", Toast.LENGTH_SHORT).show();
                        /* Intent moreIntent = new Intent(Tier2_DineIn.this, Tier3_More.class);
                        startActivity(moreIntent); */
                        break;
                    default:
                        Toast.makeText(Tier2_DineIn.this, "Error, no option selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        dineInListView.setOnItemClickListener(itemClickListener);
    }
}