package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.parse.ParseObject;

import java.text.DecimalFormat;

/**
 * Created by Sean on 4/16/16.
 */
public class ServingListItem extends OptionListItem {
    private String name;
    private double price;

    public ServingListItem(ParseObject obj) {
        super(obj.getString("info") + " " +
                new DecimalFormat("0.00").format(obj.getNumber("price").doubleValue()),
                obj.getObjectId(),
                null,
                null,
                obj.getNumber("price").doubleValue(),
                obj.getNumber("priceWithoutVat").doubleValue(),
                Double.parseDouble(obj.getString("taxClass").split("-")[1])
              );


        /* String objectOrModifierId, String modifierValueId, double price, double priceWithoutVat*/
        name = obj.getString("info");
        price = obj.getNumber("price").doubleValue();
    }

    public double getPrice() {
        return price;
    }

    public String getDisplayName() {
        return name + " " + new DecimalFormat("0.00").format(price);
    }

}
