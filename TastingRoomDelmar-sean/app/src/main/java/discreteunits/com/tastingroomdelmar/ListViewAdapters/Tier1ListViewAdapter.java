package discreteunits.com.tastingroomdelmar.ListViewAdapters;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import discreteunits.com.tastingroomdelmar.R;
import discreteunits.com.tastingroomdelmar.parseUtils.ListObject;
import discreteunits.com.tastingroomdelmar.utils.FontManager;

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

        final TextView tv = (TextView) rowView.findViewById(R.id.tv_list_item_tier1);
        tv.setText(items.get(position).getName());

        tv.setTypeface(FontManager.nexa);

        return rowView;
    }
}
