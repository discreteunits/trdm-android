package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.TypedValue;
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
public class Tier3ListViewAdapter extends ArrayAdapter<ListObject> {
    ArrayList<ListObject> items;
    Activity mContext;

    AssetManager assetManager;

    public Tier3ListViewAdapter(Activity context, ArrayList<ListObject> item) {
        super(context, R.layout.list_item_tier3, item);

        mContext = context;
        items = item;
        assetManager = mContext.getAssets();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_tier3, null, true);
        }

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        final LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.list_item_tier3);
        final TextView tv = (TextView) ll.findViewById(R.id.tv_list_item_tier3);
        tv.setText(items.get(position).getName());

        tv.setTypeface(FontManager.nexa);

        float scale = mContext.getResources().getDisplayMetrics().density;

        if (items.size() < 4) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            final int padding = (int) (55 * scale + 0.5f);
            tv.setPadding(padding,padding,padding,padding);
        } else if (items.size() == 5 || items.size() == 6) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            final int padding = (int) (38 * scale + 0.5f);
            tv.setPadding(padding,padding,padding,padding);
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            final int padding = (int) (30 * scale + 0.5f);
            tv.setPadding(padding,padding,padding,padding);
        }

        return rowView;
    }
}
