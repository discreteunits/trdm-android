package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.parse.ParseObject;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Sean on 4/16/16.
 */
public class ServingListItem extends OptionListItem {
    private String name;
    private double price;
    private String objectId;
    private DecimalFormat df = new DecimalFormat("0.00");

    public ServingListItem(ParseObject obj) {
        super(obj.getString("info") + " " +
                (CategoryManager.isDinein() ?
                        new DecimalFormat("0.00").format(obj.getNumber("deliveryPriceWithoutVat").doubleValue()) : new DecimalFormat("0.00").format(obj.getNumber("takeawayPriceWithoutVat").doubleValue())),
                obj.getObjectId(),
                null,
                null,
                obj.getNumber("price").doubleValue(),
                new double[]{obj.getNumber("deliveryPriceWithoutVat").doubleValue(),obj.getNumber("takeawayPriceWithoutVat").doubleValue()},
                new double[]{Double.parseDouble(obj.getString("deliveryTaxClass").split("-")[1]),Double.parseDouble(obj.getString("takeawayTaxClass").split("-")[1])},
                obj.getNumber("sortOrder"),
                obj.getString("info")
              );


        /* String objectOrModifierId, String modifierValueId, double price, double priceWithoutVat*/
        name = obj.getString("info");
        if (CategoryManager.isDinein())
            price = obj.getNumber("deliveryPriceWithoutVat").doubleValue();
        else
            price = obj.getNumber("takeawayPriceWithoutVat").doubleValue();
        objectId = obj.getObjectId();

        df.setRoundingMode(RoundingMode.HALF_UP);
    }

    public double getPrice() {
        return price;
    }

    public String getDisplayName() {
        return name + " " + df.format(price);
    }

    public String getObjectId() { return objectId; }
}
