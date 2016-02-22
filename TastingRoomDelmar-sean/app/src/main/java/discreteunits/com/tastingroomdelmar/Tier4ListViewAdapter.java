package discreteunits.com.tastingroomdelmar;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import parseUtils.ItemListObject;

/**
 * Created by Sean on 2/13/16.
 */
public class Tier4ListViewAdapter extends ArrayAdapter<ItemListObject> {
    ArrayList<ItemListObject> items;
    Activity mContext;

    AssetManager assetManager;

    public Tier4ListViewAdapter(Activity context, ArrayList<ItemListObject> item) {
        super(context, R.layout.list_item_tier4, item);

        mContext = context;
        items = item;
        assetManager = mContext.getAssets();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item_tier4, null, true);


        final TextView tvName = (TextView) rowView.findViewById(R.id.tv_list_item_tier4);
        final TextView tvAltName = (TextView) rowView.findViewById(R.id.tv_desc_item_tier4);
        tvName.setText(items.get(position).getName());
        tvName.setText(items.get(position).getAltName());

        final Typeface tvFont = Typeface.createFromAsset(assetManager, "fonts/nexarust/NexaRustScriptL-0.otf");
        tvName.setTypeface(tvFont);

        return rowView;
    }
}
