package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;

import java.math.RoundingMode;
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

    private String objectId;

    private boolean isBPcalced = false;
    private boolean isMPcalced = false;

    private boolean isChoice = false;
    private boolean isDineIn = true;

    private String productType;

    private double itemPriceSum = 0;
    private double itemTaxPrice = 0;

    private boolean isDiscount = false;
    private String discountType;

    private double crv = 0;

    private double taxAccumulator = 0;

    private boolean needDiscount = false;

    OrderManager mOrderManager = OrderManager.getSingleton();

    DecimalFormat df = new DecimalFormat("0.00");

    public void setObjectId(String objectId) { this.objectId = objectId; }
    public String getObjectId() { return objectId; }

    public void setIsDineIn(boolean isDineIn) { this.isDineIn = isDineIn; }
    public boolean getIsDineIn() { return isDineIn; }

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
    public String getModPrices() {
        df.setRoundingMode(RoundingMode.HALF_UP);

        if (getCRV() != 0)
            return modPrices + df.format(getWeightedCRV());
        else
            return modPrices;
    }

    public void setBaseTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getBaseTaxRate() { return this.taxRate; }

    public void updatePrices() {
        double basePrice = 0;
        if (this.basePrice != null && !isBPcalced) {
            try {
                df.setRoundingMode(RoundingMode.HALF_UP);

                //Log.d("OrderListItem", this.basePrice);
                basePrice = Double.parseDouble(this.basePrice);
                int qty = Integer.parseInt(this.qty);
                double weightedCRV = crv * qty;

                //setCRV(weightedCRV);

                basePrice = basePrice * qty;
                itemPriceSum += basePrice + weightedCRV;
                itemTaxPrice += (basePrice + weightedCRV) * (getBaseTaxRate() / 100);
                mOrderManager.addToSubTotal(basePrice + weightedCRV);
                mOrderManager.addToTax((basePrice + weightedCRV) * (getBaseTaxRate() / 100));

                this.basePrice = df.format((basePrice + weightedCRV));
                isBPcalced = true;
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (this.modPrices != null && !isMPcalced) {
            df.setRoundingMode(RoundingMode.HALF_UP);
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
                        calculatedModPrices += df.format(calculatedModPrice) + "\n";
                    }
                    else {
                        calculatedModPrice = (modPrice * qty);
                        calculatedModPrices += df.format(calculatedModPrice) + "\n";
                    }

                    itemPriceSum += calculatedModPrice;
                    itemTaxPrice += calculatedModPrice * (getBaseTaxRate() / 100);

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

    public void setIsChoice(boolean isChoice) { this.isChoice = isChoice; }

    public boolean getIsChoice() { return isChoice; }

    /* returns basePrice + modPrices */
    public double getItemPriceSum() {
        return itemPriceSum;
    }

    public double getItemTaxPrice() {
        return itemTaxPrice;
    }

    public void setCRV(double c) { crv = c; }
    public double getCRV() { return crv; }

    public double getWeightedCRV() { return crv * Integer.parseInt(qty); }

    public void setIsDiscount(boolean discount, String type) {
        isDiscount = discount;
        discountType = type;
    }
    public boolean isDiscount() { return isDiscount; }
    public String getDiscountType() { return discountType; }

    public void setNeedDiscount(boolean needDiscount) { this.needDiscount = needDiscount; }
    public boolean getNeedDiscount() { return needDiscount; }

    boolean discountApplied = false;
    public void setDiscountApplied(boolean d) { discountApplied = d; }
    public boolean getDiscountApplied() { return discountApplied; }

    String vineOrHop;
    public void setVineOrHop(String type) { vineOrHop = type; }
    public String getVineOrHop() { return vineOrHop; }
}
