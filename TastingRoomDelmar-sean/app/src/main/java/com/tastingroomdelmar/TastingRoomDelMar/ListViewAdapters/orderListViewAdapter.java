package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tastingroomdelmar.TastingRoomDelMar.Activities.MyTabActivity;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

import java.util.ArrayList;

/**
 * Created by Sean on 4/22/16.
 */
public class OrderListViewAdapter extends ArrayAdapter<OrderListItem> {
    AppCompatActivity mContext;
    ArrayList<OrderListItem> item;

    public OrderListViewAdapter(AppCompatActivity context, ArrayList<OrderListItem> item) {
        super(context, R.layout.list_item_mytab, item);

        mContext = context;
        this.item = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyTabActivity.OrderItemViewHolder viewHolder;
        View rowView = convertView;

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        if (rowView == null || rowView.getTag() == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_mytab, null, true);

            viewHolder = new MyTabActivity.OrderItemViewHolder();

            viewHolder.tvQty = (TextView) rowView.findViewById(R.id.mytab_list_item_qty);
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.mytab_list_item_name);
            viewHolder.tvOption = (TextView) rowView.findViewById(R.id.mytab_list_item_option);
            viewHolder.tvBasePrice = (TextView) rowView.findViewById(R.id.mytab_list_item_price);
            viewHolder.tvModPrice = (TextView) rowView.findViewById(R.id.mytab_list_item_price_mod);

            viewHolder.tvQty.setTypeface(FontManager.nexa);
            viewHolder.tvOption.setTypeface(FontManager.nexa);
            viewHolder.tvBasePrice.setTypeface(FontManager.nexa);
            viewHolder.tvModPrice.setTypeface(FontManager.nexa);

            viewHolder.tvName.setTypeface(FontManager.bebasReg);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (MyTabActivity.OrderItemViewHolder) rowView.getTag();
        }

        OrderListItem orderListItem = item.get(position);

        viewHolder.tvQty.setText(orderListItem.getQty());
        viewHolder.tvName.setText(orderListItem.getName());

        String options = orderListItem.getOptions();

        if (orderListItem.getIsDineIn()) {
            if (options != null)
                viewHolder.tvOption.setText(options);
            else
                viewHolder.tvOption.setVisibility(View.GONE);
        }
        else {
            if (options != null)
                viewHolder.tvOption.setText(options + "\nTakeaway");
            else
                viewHolder.tvOption.setText("Takeaway");
        }

        viewHolder.tvBasePrice.setText(orderListItem.getBasePrice());
        viewHolder.tvModPrice.setText(orderListItem.getModPrices());

        return rowView;
    }
}
