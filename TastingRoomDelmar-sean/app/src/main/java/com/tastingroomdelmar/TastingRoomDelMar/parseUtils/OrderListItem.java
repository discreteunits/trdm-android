package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import java.text.DecimalFormat;

/**
 * Created by Sean on 4/22/16.
 */
public class OrderListItem {
    private String qty;
    private String name;
    private String options;
    private String basePrice;
    private String modPrices;

    private double subTotal;
    private double tax;
    private double total;

    public void setQty(String qty) { this.qty = qty; }
    public String getQty() { return qty; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setOptions(String options) {
        if (this.options != null)
            this.options = this.options + options;
        else
            this.options = options;
    }
    public String getOptions() { return options; }

    public void setBasePrice(String basePrice) {
        try {
            if (basePrice != null && !basePrice.equals("null")) {
                double calculatedPrice = Double.parseDouble(basePrice) * Integer.parseInt(getQty());
                subTotal += calculatedPrice;
                this.basePrice = new DecimalFormat("0.##").format(calculatedPrice);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public String getBasePrice() { return basePrice; }

    public void setModPrices(String modPrices) {
        try {
            if (this.modPrices != null) {
                if (modPrices != null && !modPrices.equals("\n")) {
                    double calculatedPrice = Double.parseDouble(modPrices.split("\\n")[0]) * Integer.parseInt(getQty());
                    subTotal += calculatedPrice;
                    this.modPrices = this.modPrices + new DecimalFormat("0.##").format(calculatedPrice);
                }
            } else {
                this.modPrices = modPrices;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public String getModPrices() { return modPrices; }
}
