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
public class Tier4TopListViewAdapter extends ArrayAdapter<String> {
    ArrayList<String> items;
    Activity mContext;

    AssetManager assetManager;

    public Tier4TopListViewAdapter(Activity context, ArrayList<String> item) {
        super(context, R.layout.list_item_tier4_top, item);

        mContext = context;
        items = item;
        assetManager = mContext.getAssets();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item_tier4_top, null, true);


        final TextView tv = (TextView) rowView.findViewById(R.id.tv_list_item_tier4_top);
        tv.setText(items.get(position));

        final Typeface tvFont = Typeface.createFromAsset(assetManager, "fonts/nexarust/NexaRustScriptL-0.otf");
        tv.setTypeface(tvFont);

        return rowView;
    }
}
