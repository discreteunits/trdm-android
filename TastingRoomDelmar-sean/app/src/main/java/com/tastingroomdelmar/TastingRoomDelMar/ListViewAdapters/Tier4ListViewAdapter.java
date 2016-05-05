package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.Activities.Tier4Activity;
import com.tastingroomdelmar.TastingRoomDelMar.Dialogs.ModalDialog;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ItemListObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by Sean on 2/13/16.
 */
public class Tier4ListViewAdapter extends ArrayAdapter<ItemListObject> implements Filterable {
    private static final String TAG = Tier4ListViewAdapter.class.getSimpleName();

    ArrayList<ItemListObject> original;
    ArrayList<ItemListObject> items;
    AppCompatActivity mContext;

    CategoryFilter filter;

    boolean isEvent;

    public Tier4ListViewAdapter(AppCompatActivity context, ArrayList<ItemListObject> item) {
        super(context, R.layout.list_item_tier4, item);

        mContext = context;
        items = item;
        original = item;

        isEvent = ((TextView) mContext.findViewById(R.id.tv_curr_activity)).getText().equals("Events");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tier4Activity.ItemViewHolder viewHolder;
        View rowView = convertView;

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        if (rowView == null || rowView.getTag() == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_tier4, null, true);

            viewHolder = new Tier4Activity.ItemViewHolder();

            viewHolder.tvName = (TextView) rowView.findViewById(R.id.tv_list_item_tier4);
            viewHolder.tvInfo = (TextView) rowView.findViewById(R.id.tv_desc_item_tier4);
            viewHolder.tvVerietal = (TextView) rowView.findViewById(R.id.tv_wine_var_tier4);
            viewHolder.tvOption = (TextView) rowView.findViewById(R.id.tv_select_opt_tier4);
            viewHolder.btnAddToTab = (Button) rowView.findViewById(R.id.button_add_to_tab_tier4);

            viewHolder.tvName.setTypeface(FontManager.bebasReg);
            viewHolder.tvInfo.setTypeface(FontManager.openSansLight);
            viewHolder.tvOption.setTypeface(FontManager.openSansItalic);
            viewHolder.btnAddToTab.setTypeface(FontManager.nexa);

            rowView.setTag(viewHolder);

        } else {
            viewHolder = (Tier4Activity.ItemViewHolder) rowView.getTag();
        }

        final ItemListObject item = items.get(position);

        //TODO TODO TODO TODO___PHASE 2___TODO TODO TODO TODO//
        viewHolder.tvVerietal.setVisibility(View.GONE);
        //TODO TODO TODO TODO___PHASE 2___TODO TODO TODO TODO//

        if (item != null) {
            viewHolder.tvName.setText(item.getName());
            viewHolder.tvInfo.setText(item.getAltName());
            if (CategoryManager.isDinein())
                viewHolder.tvOption.setText(item.getPriceArray()[0]);
            else
                viewHolder.tvOption.setText(item.getPriceArray()[1]);
            viewHolder.tvVerietal.setText(item.getVerietal());

            viewHolder.btnAddToTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModalDialog modalDialog = new ModalDialog(mContext, item, isEvent);
                    modalDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    modalDialog.show();
                }
            });
        }

        return rowView;
    }

    @Override
    public int getCount() {
        if (items != null) return items.size();
        else return 0;
    }

    @Override
    public ItemListObject getItem(int pos) {
        if (items != null) return items.get(pos);
        else return null;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CategoryFilter();
        }

        return filter;


    }

    private class CategoryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d(TAG, "filtering for: " + constraint);

            ArrayList<ItemListObject> tmpList = new ArrayList<>(original);
            FilterResults result = new FilterResults();

            if(constraint == null || constraint.length() == 0 || constraint == ("Full List")) {
                result.values = original;
                result.count = original.size();
            } else {
                ArrayList<ItemListObject> filteredList = new ArrayList<>();

                Log.d(TAG, "original count: " + tmpList.size());

                for(ItemListObject obj : tmpList) {
                    if(obj.getVerietal().contains(constraint))
                        filteredList.add(obj);
                }

                result.values = filteredList;
                result.count = filteredList.size();
            }

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            items = (ArrayList<ItemListObject>) results.values;
            notifyDataSetChanged();
        }
    }
}
