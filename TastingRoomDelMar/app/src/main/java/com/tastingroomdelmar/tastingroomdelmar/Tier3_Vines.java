package com.tastingroomdelmar.tastingroomdelmar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tier3_Vines extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tier3_vines);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_back); */

        TextView up_nav = (TextView) findViewById(R.id.up_nav_textView);
        up_nav.setText(R.string.title_activity_tier2__dine_in);
        /* An alternative to getPrevActivityName() may be getParentActivityName()
        TextView up_nav = (TextView) findViewById(R.id.up_nav_textView);
        up_nav.setText(PrevActivity.getPrevActivityName()); */

        TextView toolbar_header = (TextView) findViewById(R.id.toolbar_header);
        toolbar_header.setText(R.string.title_activity_tier3__vines);

        listView = (ListView) findViewById(R.id.tier3_listView);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                switch (position) {
                    case 0:
                        /* Toast.makeText(Tier3_Vines.this, "Starts Reds activity", Toast.LENGTH_SHORT).show(); */
                        Intent redsIntent = new Intent(Tier3_Vines.this, Tier4_Reds.class);
                        startActivity(redsIntent);
                        break;
                    case 1:
                        Toast.makeText(Tier3_Vines.this, "Starts Whites activity", Toast.LENGTH_SHORT).show();
                        /* Intent takeOutIntent = new Intent(Tier3_Vines.this, Hops.class);
                        startActivity(takeOutIntent); */
                        break;
                    case 2:
                        Toast.makeText(Tier3_Vines.this, "Starts Sparkling activity", Toast.LENGTH_SHORT).show();
                        /* Intent eventsIntent = new Intent(Tier3_Vines.this, Harvest.class);
                        startActivity(eventsIntent); */
                        break;
                    case 3:
                        Toast.makeText(Tier3_Vines.this, "Starts Rose activity", Toast.LENGTH_SHORT).show();
                        /* Intent eventsIntent = new Intent(Tier3_Vines.this, More.class);
                        startActivity(eventsIntent); */
                        break;
                    case 4:
                        Toast.makeText(Tier3_Vines.this, "Starts Dessert activity", Toast.LENGTH_SHORT).show();
                        /* Intent eventsIntent = new Intent(Tier3_Vines.this, Harvest.class);
                        startActivity(eventsIntent); */
                        break;
                    case 5:
                        Toast.makeText(Tier3_Vines.this, "Starts Flights activity", Toast.LENGTH_SHORT).show();
                        /* Intent eventsIntent = new Intent(Tier3_Vines.this, More.class);
                        startActivity(eventsIntent); */
                        break;
                    default:
                        Toast.makeText(Tier3_Vines.this, "Error, no option selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

    public void goToParentActivity(View view) {
        Intent parentActivityIntent = new Intent(Tier3_Vines.this, Tier2_DineIn.class);
        startActivity(parentActivityIntent);
    }
}