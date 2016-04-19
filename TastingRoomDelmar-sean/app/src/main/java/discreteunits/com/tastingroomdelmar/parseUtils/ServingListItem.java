package discreteunits.com.tastingroomdelmar.parseUtils;

import com.parse.ParseObject;

import java.text.DecimalFormat;

/**
 * Created by Sean on 4/16/16.
 */
public class ServingListItem extends OptionListItem {
    private Number price;

    public ServingListItem(ParseObject obj) {
        super(obj.getString("info") + " " +
                new DecimalFormat("0.##").format(obj.getNumber("price").doubleValue()));

        price = obj.getNumber("price");
    }

    public Number getPrice() {
        return price;
    }

}
