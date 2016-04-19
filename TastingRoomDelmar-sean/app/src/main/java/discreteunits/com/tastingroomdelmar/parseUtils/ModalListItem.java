package discreteunits.com.tastingroomdelmar.parseUtils;

import java.util.ArrayList;

import discreteunits.com.tastingroomdelmar.utils.Constants;

/**
 * Created by Sean on 4/15/16.
 */
public class ModalListItem {
    private String title;
    private ArrayList<OptionListItem> optionList;
    private ArrayList<OptionListItem> quantityList;

    private Constants.Type type;

    public ModalListItem(Constants.Type type, String title, ArrayList<OptionListItem> optionList) {
        this.title = title;
        this.optionList = optionList;

        this.type = type;
    }

    public ModalListItem() {
        this.title = Constants.QUANTITY;

        quantityList = new ArrayList<>();

        for (int i = 1; i < 11; i++) quantityList.add(new OptionListItem((i+"")));

        type = Constants.Type.QUANTITY;
    }

    public String getTitle() { return title; }

    public ArrayList<OptionListItem> getOptionList() { return optionList; }

    public ArrayList<OptionListItem> getQuantityList() { return quantityList; }

    public Constants.Type getType() { return type; }
}
