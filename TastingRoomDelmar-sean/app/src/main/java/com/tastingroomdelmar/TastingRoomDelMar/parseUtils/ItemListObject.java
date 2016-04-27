package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.tastingroomdelmar.TastingRoomDelMar.utils.Constants;

/**
 * Created by Sean on 2/14/16.
 */
public class ItemListObject implements Comparable<ItemListObject> {
    private static final String TAG = ItemListObject.class.getSimpleName();

    private String name;
    private String altName;
    private String objectId;
    private String prices;
    private double taxRate;
    private String verietal;
    private String productType;
    private JSONArray categoryArray;
    private JSONArray additionsArray;
    private JSONArray servingsArray;

    private String additionTitle;

    private ArrayList<OptionListItem> servingList;
    private ArrayList<OptionListItem> additionList;

    private ArrayList<ModalListItem> additionOptions;

    public ItemListObject(ParseObject obj) {
        objectId = obj.getObjectId();
        name = obj.getString("name");
        altName = obj.getString("info");
        prices = obj.getString("prices");
        productType = obj.getString("productType");
        taxRate = Double.parseDouble(obj.getString("taxClass").split("-")[1]);
        if (prices == null || prices.isEmpty()) prices = obj.getNumber("price") + "";
        categoryArray = obj.getJSONArray("categories");
        additionsArray = obj.getJSONArray("additions");
        servingsArray = obj.getJSONArray("options");

        if (servingsArray != null) storeServings();
        if (additionsArray != null) storeAdditions();
    }

    public String getName() {
        return name;
    }

    public String getObjectId() { return objectId; }

    public String getAltName() {
        return altName;
    }

    public String getPrices() { return prices; }

    public JSONArray getCategoryArray() { return categoryArray; }

    public String getVerietal() { return verietal; }

    public void setVerietal(String v) {
        verietal = v;
    }

    public String getProductType() { return productType; }

    public double getTaxRate() { return taxRate; }

    private void storeServings() {
        servingList = new ArrayList<>();

        for (int i = 0; i < servingsArray.length(); i++) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
            try {
                query.getInBackground(servingsArray.getString(i), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            servingList.add(new ServingListItem(object));
                            Log.d(TAG, "Serving: " + object.getString("info"));

                            Collections.sort(servingList, new MyComparator());
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

//        /* do a null check on first item on the list and make it selected */
//        OptionListItem firstItem = servingList.size() == 0 ? null : servingList.get(0);
//        if (firstItem != null) firstItem.setSelected(true);
    }

    private void storeAdditions() {
        try {
            additionOptions = new ArrayList<>();

            for (int i = 0; i < additionsArray.length(); i++) {
                final String additionName = additionsArray.getJSONObject(i).getString("displayName");
                final String modifierId = additionsArray.getJSONObject(i).getString("id");

                if (!additionName.equals("")) additionTitle = additionName;
                else continue;

                Log.d(TAG, "Addition Name: " + additionName);
                final JSONArray valueObject = additionsArray.getJSONObject(i).getJSONArray("values");

                /* check if this entry has any value to it */
                if (valueObject.length() == 0) continue;

                additionList = new ArrayList<>();
                for (int j = 0; j < valueObject.length(); j++) {
                    String additionId = valueObject.getJSONObject(j).getString("id");
                    String additionDetailName = valueObject.getJSONObject(j).getString("name");
                    String additionDetailPrice = valueObject.getJSONObject(j).getString("price");
                    String additionDetailPriceWithoutVat = valueObject.getJSONObject(j).getString("priceWithoutVAT");

                    if (additionDetailPrice.equals("0")) additionDetailPrice = "";
                    else additionDetailPrice = "  +" + additionDetailPrice;

                    additionList.add(new AdditionListItem(additionDetailName + additionDetailPrice, getObjectId(),modifierId, additionId,
                            !additionDetailPrice.equals("") ? Double.parseDouble(additionDetailPrice): 0,
                            !additionDetailPriceWithoutVat.equals("") ? Double.parseDouble(additionDetailPriceWithoutVat) : 0,
                            this.taxRate));

                    Log.d(TAG, "addition detail name: " + additionDetailName);
                    Collections.sort(additionList, new MyComparator());
                }

                /* do a null check on first item on the list and make it selected */
                OptionListItem firstItem = additionList.size() == 0 ? null : additionList.get(0);
                if (firstItem != null) firstItem.setSelected(true);

                String basePrice =  getPrices().split(",")[0];
                if (basePrice == null) basePrice = getPrices();

                basePrice = new DecimalFormat("0.00").format(Double.parseDouble(basePrice));

                additionOptions.add(new ModalListItem(getObjectId(), getName(), basePrice, getTaxRate(), Constants.Type.ADDITION, Constants.OTHER, getAdditionTitle(), additionList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAdditionTitle() {
        if (additionTitle == null || additionTitle.isEmpty()) return "OPTIONS";

        return additionTitle;
    }

    public ArrayList<OptionListItem> getServings() {
        if (servingList != null) {
            for (OptionListItem item: servingList) { item.setSelected(false); }
            servingList.get(0).setSelected(true);
        }
        return servingList;
    }

    public ArrayList<ModalListItem> getAdditions() {
        return additionOptions;
    }

    @Override
    public int compareTo(ItemListObject another) {
        return name.compareTo(another.getName());
    }

    private class MyComparator implements Comparator<OptionListItem> {
        @Override
        public int compare(OptionListItem i1, OptionListItem i2) {
            return Double.valueOf(i1.getPrice()).compareTo(i2.getPrice());
        }
    }
}
