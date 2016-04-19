package discreteunits.com.tastingroomdelmar.parseUtils;

/**
 * Created by Sean on 4/15/16.
 */
public class OptionListItem {
    private String optionName;

    private boolean isSelected;

    public OptionListItem(String optionName) {
        this.optionName = optionName;
        this.isSelected = false;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() { return isSelected; }




}
