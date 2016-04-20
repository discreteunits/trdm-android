package discreteunits.com.tastingroomdelmar.ListViewAdapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import discreteunits.com.tastingroomdelmar.R;
import discreteunits.com.tastingroomdelmar.parseUtils.OptionListItem;
import discreteunits.com.tastingroomdelmar.utils.FontManager;

/**
 * Created by Sean on 4/14/16.
 */
public class OptionListViewAdapter extends ArrayAdapter<OptionListItem> {
    AppCompatActivity mContext;
    ArrayList<OptionListItem> options;

    public OptionListViewAdapter(AppCompatActivity context, ArrayList<OptionListItem> options) {
        super(context, R.layout.layout_modal, options);

        mContext = context;
        this.options = options;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_option, null, true);
        }

        if (FontManager.getSingleton() == null) new FontManager(mContext.getApplicationContext());

        TextView optionName = (TextView) rowView.findViewById(R.id.modal_option_name);

        optionName.setText(options.get(position).getOptionName());
        optionName.setTypeface(FontManager.nexa);

        if (options.get(position).isSelected()) {
            optionName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.black_soft_corner_button));
            optionName.setTextColor(Color.WHITE);
        } else {
            optionName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.white_soft_corner_button));
            optionName.setTextColor(Color.BLACK);
        }

        return rowView;
    }

    @Override
    public int getCount() {
        if (options != null) return options.size();
        else return 0;
    }

    @Override
    public OptionListItem getItem(int pos) {
        if (options != null) return options.get(pos);
        else return null;
    }
}
