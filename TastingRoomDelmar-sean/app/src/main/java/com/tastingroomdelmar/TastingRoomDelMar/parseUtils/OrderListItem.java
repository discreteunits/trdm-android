package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;

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
    private double taxRate;

    private boolean isBPcalced = false;
    private boolean isMPcalced = false;

    private String productType;

    OrderManager mOrderManager = OrderManager.getSingleton();

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
                this.basePrice = basePrice;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public String getBasePrice() { return basePrice; }

    public void setProductType(String pType) { productType = pType; }

    public String getProductType() { return productType; }

    public void setModPrices(String modPrices) {
        try {
            if (this.modPrices != null) {
                if (modPrices != null && !modPrices.equals("\n")) {

                    this.modPrices += modPrices;
                }
            } else {
                this.modPrices = modPrices;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public String getModPrices() { return modPrices; }

    public void setBaseTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getBaseTaxRate() { return this.taxRate; }

    public void updatePrices() {
        double basePrice = 0;
        if (this.basePrice != null && !isBPcalced) {
            try {
                //Log.d("OrderListItem", this.basePrice);
                basePrice = Double.parseDouble(this.basePrice);
                int qty = Integer.parseInt(this.qty);

                basePrice = basePrice * qty;
                mOrderManager.addToSubTotal(basePrice);
                mOrderManager.addToTax(basePrice * (getBaseTaxRate() / 100));
                this.basePrice = new DecimalFormat("0.00").format((basePrice));
                isBPcalced = true;
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (this.modPrices != null && !isMPcalced) {
            String[] modPrices = this.modPrices.split("\\n");
            String calculatedModPrices = "";
            for (int i = 0; i < modPrices.length; i++) {
                try {
                    String mp = modPrices[i];

                    double modPrice = Double.parseDouble(mp);
                    int qty = Integer.parseInt(this.qty);
                    double calculatedModPrice;

                    if (getProductType() != null && getProductType().equals(Constants.CHOICE)) {
                        calculatedModPrice = (modPrice * qty) - basePrice;
                        calculatedModPrices += new DecimalFormat("0.00").format(calculatedModPrice) + "\n";
                    }
                    else {
                        calculatedModPrice = (modPrice * qty);
                        calculatedModPrices += new DecimalFormat("0.00").format(calculatedModPrice) + "\n";
                    }

                    mOrderManager.addToSubTotal(calculatedModPrice);
                    mOrderManager.addToTax(calculatedModPrice * (getBaseTaxRate() / 100));

                    isMPcalced = true;

                } catch (NumberFormatException | NullPointerException e) {
                    e.printStackTrace();
                    calculatedModPrices += "\n";
                }
            }

            this.modPrices = calculatedModPrices;
        }
    }
}
