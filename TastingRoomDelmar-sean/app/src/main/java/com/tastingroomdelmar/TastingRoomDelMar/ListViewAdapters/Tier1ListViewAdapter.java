package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by Sean on 2/13/16.
 */
public class Tier1ListViewAdapter extends ArrayAdapter<ListObject> {
    ArrayList<ListObject> items;
    AppCompatActivity mContext;

    AssetManager assetManager;

    public Tier1ListViewAdapter(AppCompatActivity context, ArrayList<ListObject> item) {
        super(context, R.layout.list_item_tier1, item);

        mContext = context;
        items = item;
        assetManager = mContext.getAssets();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_tier1, null, true);
        }

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        final LinearLayout ll = (LinearLayout) rowView;
        final TextView tv = (TextView) ll.findViewById(R.id.tv_list_item_tier1);

        if (tv != null) {
            tv.setText(items.get(position).getName());

            tv.setTypeface(FontManager.nexa);
        }

        return rowView;
    }
}
