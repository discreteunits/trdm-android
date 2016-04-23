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
    private String baseItemPrice;

    public ModalListItem(String baseItemName, String baseItemPrice, Constants.Type type, String title, ArrayList<OptionListItem> optionList) {
        this.title = title;
        this.optionList = optionList;

        this.type = type;
        this.baseItemName = baseItemName;
        this.baseItemPrice = baseItemPrice;
    }

    public ModalListItem(String baseItemName, String baseItemPrice, String objectId) {
        this.title = Constants.QUANTITY;
        this.baseItemName = baseItemName;
        this.baseItemPrice = baseItemPrice;

        quantityList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            quantityList.add(new OptionListItem((i+""), objectId));
        }

        quantityList.get(0).setSelected(true);

        type = Constants.Type.QUANTITY;
    }

    public ModalListItem(String baseItemName, String baseItemPrice) {
        this.title = Constants.QUANTITY;
        this.baseItemName = baseItemName;
        this.baseItemPrice = baseItemPrice;

        quantityList = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            quantityList.add(new OptionListItem((i+"")));
        }

        quantityList.get(0).setSelected(true);

        type = Constants.Type.QUANTITY;
    }

    public String getTitle() { return title; }

    public ArrayList<OptionListItem> getOptionList() { return optionList; }

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

    public String getBaseItemPrice() { return baseItemPrice; }
}
