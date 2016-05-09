package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import java.util.ArrayList;

import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;

/**
 * Created by Sean on 4/15/16.
 */
public class ModalListItem {
    private String title;
    private ArrayList<OptionListItem> optionList;
    private ArrayList<OptionListItem> quantityList;

    private Constants.Type type;

    private String baseItemName;
    private String[] baseItemPrice;
    private String baseObjectId;
    private double[] baseTaxRate;

    private String productType;

    private Number sortOrder;

    public ModalListItem(String baseObjectId, String baseItemName, String[] baseItemPrice, double[] baseTaxRate, Constants.Type type, String pType, String title, ArrayList<OptionListItem> optionList) {
        this.baseObjectId = baseObjectId;
        this.title = title;
        this.optionList = optionList;

        this.type = type;
        this.baseItemName = baseItemName;
        this.baseItemPrice = baseItemPrice;
        this.baseTaxRate = baseTaxRate;
        this.productType = pType;
    }

    public ModalListItem(String baseItemName, String[] baseItemPrice, double[] baseTaxRate, String pType, String objectId) {
        this.title = Constants.QUANTITY;
        this.baseItemName = baseItemName;
        this.baseItemPrice = baseItemPrice;
        this.baseTaxRate = baseTaxRate;
        this.productType = pType;
        this.baseObjectId = objectId;


        quantityList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            quantityList.add(new OptionListItem((i+""), objectId, new double[]{0,0}, null));
        }

        quantityList.get(0).setSelected(true);

        type = Constants.Type.QUANTITY;
    }

    public String getTitle() { return title; }

    public ArrayList<OptionListItem> getOptionList() {
        if (optionList != null) {
            for (OptionListItem item : optionList) { item.setSelected(false); }
            optionList.get(0).setSelected(true);
        }
        return optionList;
    }

    public ArrayList<OptionListItem> getQuantityList() { return quantityList; }

    public Constants.Type getType() { return type; }

    public OptionListItem getSelectedOptionItem() throws Exception {
        if (getType() == Constants.Type.QUANTITY) {
            for (OptionListItem optionItem : quantityList) {
                if (optionItem.isSelected()) return optionItem;
            }
        } else {
            for (OptionListItem optionItem : optionList) {
                if (optionItem.isSelected()) return optionItem;
            }
        }

        throw new Exception("There is no selected option");
    }

    public String getBaseItemName() { return baseItemName; }

    public String[] getBaseItemPrice() { return baseItemPrice; }

    public String getProductType() { return productType; }

    public String getBaseObjectId() { return baseObjectId; }

    public double[] getBaseTaxRate() { return baseTaxRate; }
}
