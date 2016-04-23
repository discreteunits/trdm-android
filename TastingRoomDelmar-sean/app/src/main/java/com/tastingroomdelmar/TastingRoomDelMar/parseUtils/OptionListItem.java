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
    private double priceWithoutVat;

    /* This is for both servings and additions */
    public OptionListItem(String optionName, String objectId, String modifierId, String modifierValueId, double price, double priceWithoutVat) {
        this.optionName = optionName;
        this.isSelected = false;

        this.objectId = objectId;
        this.modifierId = modifierId;
        this.modifierValueId = modifierValueId;
        this.price = price;
        this.priceWithoutVat = priceWithoutVat;
    }

    /* This is for items that do not have both Servings and options. (e.g. Merch, Tickets, etc) */
    public OptionListItem(String quantityNumber, String objectId) {
        this.optionName = quantityNumber;
        this.isSelected = false;
        this.objectId = objectId;
    }

    /* This is for quantity options */
    public OptionListItem(String quantityNumber) {
        this.optionName = quantityNumber;
        this.isSelected = false;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() { return isSelected; }

    public String getObjectId() { return objectId; }

    public String getModifierId() { return modifierId; }

    public String getModifierValueId() { return modifierValueId; }

    public double getPrice() { return price; }

    public double getPriceWithoutVat() { return priceWithoutVat; }

}
