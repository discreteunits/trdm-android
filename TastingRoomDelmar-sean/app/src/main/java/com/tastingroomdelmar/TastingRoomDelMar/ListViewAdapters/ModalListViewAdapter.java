package com.tastingroomdelmar.TastingRoomDelMar.ListViewAdapters;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.ModalListItem;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OptionListItem;
import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.FontManager;

/**
 * Created by Sean on 4/15/16.
 */
public class ModalListViewAdapter extends ArrayAdapter<ModalListItem> {
    private static final String TAG = ModalListViewAdapter.class.getSimpleName();
    ArrayList<ModalListItem> items;
    AppCompatActivity mContext;

    //ArrayList<OptionListItem> optionItems;
    //OptionListViewAdapter adapter;
    public ModalListViewAdapter(AppCompatActivity context, ArrayList<ModalListItem> items) {
        super(context, R.layout.layout_modal, items);

        mContext = context;
        this.items = items;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_modal, null, true);
        }

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        final TextView title = (TextView) rowView.findViewById(R.id.modal_tv_category);
        final TwoWayView listView = (TwoWayView) rowView.findViewById(R.id.modal_lv_choice);

        final ArrayList<OptionListItem> optionItems = new ArrayList<>();
        final OptionListViewAdapter adapter = new OptionListViewAdapter(mContext, optionItems);
        listView.setAdapter(adapter);

        final int pos = position;

        final ModalListItem item = items.get(position);
        title.setText(item.getTitle());

        title.setTypeface(FontManager.bebasReg);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "position: " + pos + " - " + position);
                selectOption(item, adapterView.getItemAtPosition(position), adapter);
            }
        });

        //Log.d(TAG, "Type: " + item.getType());

        if (item.getType() == Constants.Type.QUANTITY) {
            ArrayList<OptionListItem> optionListItems = item.getQuantityList();
            if (optionListItems != null) {
                optionItems.clear();
                for (OptionListItem oli : optionListItems) optionItems.add(oli);

                adapter.notifyDataSetChanged();
            }
        } else {
            ArrayList<OptionListItem> optionListItems = item.getOptionList();
            if (optionListItems != null) {
                optionItems.clear();
                for(OptionListItem oli : optionListItems) optionItems.add(oli);

                adapter.notifyDataSetChanged();
            }
        }

        return rowView;
    }


    private void selectOption(ModalListItem modalItem, Object optionItem, OptionListViewAdapter adapter) {
        ArrayList<OptionListItem> optionList = modalItem.getOptionList();
        if (optionList == null) optionList = modalItem.getQuantityList();

        if (optionList != null) {
            if (!((OptionListItem) optionItem).isSelected()) {
                for (OptionListItem oli : optionList) {
                    oli.setSelected(false);
                }

                ((OptionListItem) optionItem).setSelected(true);
            } else {
                for (OptionListItem oli : optionList) {
                    oli.setSelected(false);
                }
            }

            Log.d(TAG, "selected option name: " + ((OptionListItem) optionItem).getOptionName());
            adapter.notifyDataSetChanged();
        }
    }
}
