package com.tastingroomdelmar.tastingroomdelmar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Tier1 extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tier1_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /* TextView tv_prevAct = (TextView) findViewById(R.id.prevAct);
        tv_prevAct.setText(PrevActivity.getPrevActivityName()); */

        listView = (ListView) findViewById(R.id.tier1_listView);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                switch (position) {
                    case 0:
                        /* Toast.makeText(Tier1.this, "Starts DineIn.java activity", Toast.LENGTH_SHORT).show(); */
                        Intent dineInIntent = new Intent(Tier1.this, Tier2_DineIn.class);
                        startActivity(dineInIntent);
                        break;
                    case 1:
                        /* Toast.makeText(Tier1.this, "Starts TakeOut.java activity", Toast.LENGTH_SHORT).show(); */
                        Intent takeOutIntent = new Intent(Tier1.this, Tier2_TakeOut.class);
                        startActivity(takeOutIntent);
                        break;
                    case 2:
                        Toast.makeText(Tier1.this, "Starts Events activity", Toast.LENGTH_SHORT).show();
                        /* Intent eventsIntent = new Intent(Tier1.this, Events.class);
                        startActivity(eventsIntent); */
                        break;
                    default:
                        Toast.makeText(Tier1.this, "Error, no option selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

}