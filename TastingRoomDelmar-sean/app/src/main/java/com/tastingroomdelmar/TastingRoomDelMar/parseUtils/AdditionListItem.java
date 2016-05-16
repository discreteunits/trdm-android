package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

/**
 * Created by Sean on 4/16/16.
 */
public class AdditionListItem extends OptionListItem {
    public String objectId;
    public String modifierId;
    public String modifierValueId;
    public double price;
    public double[] priceWithoutVat;
    public double[] taxRate;
    public String info;
    public Number sortOrder;

    public AdditionListItem(String name, String objectId, String modifierId, String modifierValueId, double price, double[] priceWithoutVat, double[] taxRate, Number sortOrder, String info) {
        super(name, objectId, modifierId, modifierValueId, price, priceWithoutVat, taxRate, sortOrder, info);

        this.objectId = objectId;
        this.modifierId = modifierId;
        this.modifierValueId = modifierValueId;
        this.price = price;
        this.priceWithoutVat = priceWithoutVat;
        this.taxRate = taxRate;
        this.sortOrder = sortOrder;
        this.info = info;
    }

    public String getObjectId() { return this.objectId; }


}
