package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

/**
 * Created by Sean on 4/15/16.
 */
public class OptionListItem {
    private String optionName;

    private boolean isSelected;

    private String objectId;
    private String modifierId;
    private String modifierValueId;
    private double price;
    private double[] priceWithoutVat;
    private double[] taxRate;
    private String info;

    private Number sortOrder;

    /* This is for both servings and additions */
    public OptionListItem(String optionName, String objectId, String modifierId, String modifierValueId, double price, double[] priceWithoutVat, double[] taxRate, Number sortOrder, String info) {
        this.optionName = optionName;
        this.isSelected = false;

        this.objectId = objectId;
        this.modifierId = modifierId;
        this.modifierValueId = modifierValueId;
        this.price = price;
        this.priceWithoutVat = priceWithoutVat;
        this.taxRate = taxRate;
        this.info = info;
    }

    /* This is for items that do not have both Servings and options. (e.g. Merch, Tickets, etc) */
    public OptionListItem(String quantityNumber, String objectId, double[] taxRate, Number sortOrder) {
        this.optionName = quantityNumber;
        this.isSelected = false;
        this.objectId = objectId;
        this.taxRate = taxRate;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() { return isSelected; }

    public String getBaseObjectId() { return objectId; }

    public String getModifierId() { return modifierId; }

    public String getModifierValueId() { return modifierValueId; }

    public double getPrice() { return price; }

    public double[] getPriceWithoutVat() { return priceWithoutVat; }

    public double[] getTaxRate() { return taxRate; }

    public Number getSortOrder() { return sortOrder; }

    public String getInfo() { return info; }

}
