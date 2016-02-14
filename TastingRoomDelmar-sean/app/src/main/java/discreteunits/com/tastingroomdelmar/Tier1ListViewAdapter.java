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

import parseUtils.ListObject;

/**
 * Created by Sean on 2/13/16.
 */
public class Tier1ListViewAdapter extends ArrayAdapter<ListObject> {
    ArrayList<ListObject> items;
    Activity mContext;

    AssetManager assetManager;

    public Tier1ListViewAdapter(Activity context, ArrayList<ListObject> item) {
        super(context, R.layout.list_item_tier1, item);

        mContext = context;
        items = item;
        assetManager = mContext.getAssets();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item_tier1, null, true);


        final TextView tv = (TextView) rowView.findViewById(R.id.tv_list_item_tier1);
        tv.setText(items.get(position).getName());

        final Typeface tvFont = Typeface.createFromAsset(assetManager, "fonts/nexarust/NexaRustScriptL-0.otf");
        tv.setTypeface(tvFont);

        return rowView;
    }
}
