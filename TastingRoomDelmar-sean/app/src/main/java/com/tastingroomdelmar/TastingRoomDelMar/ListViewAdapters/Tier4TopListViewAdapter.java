package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.TopListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by Sean on 2/13/16.
 */
public class Tier4TopListViewAdapter extends ArrayAdapter<TopListObject> {
    ArrayList<TopListObject> items;
    AppCompatActivity mContext;

    public Tier4TopListViewAdapter(AppCompatActivity context, ArrayList<TopListObject> item) {
        super(context, R.layout.list_item_tier4_top, item);

        mContext = context;
        items = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_tier4_top, null, true);
        }

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        final TextView tv = (TextView) rowView.findViewById(R.id.tv_list_item_tier4_top);
        tv.setText(items.get(position).getName());

        tv.setTypeface(FontManager.nexa);

        if (items.get(position).isSelected()) {
            tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.black_soft_corner_button));
            tv.setTextColor(Color.WHITE);
        } else {
            tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.white_soft_corner_button));
            tv.setTextColor(Color.BLACK);
        }

        return rowView;
    }
}
