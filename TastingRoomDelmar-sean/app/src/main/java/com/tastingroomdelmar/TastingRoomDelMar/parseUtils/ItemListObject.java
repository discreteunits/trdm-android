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
    private String verietal;
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
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        /* do a null check on first item on the list and make it selected */
        OptionListItem firstItem = servingList.size() == 0 ? null : servingList.get(0);
        if(firstItem != null) firstItem.setSelected(true);
    }

    private void storeAdditions() {
        try {
            additionOptions = new ArrayList<>();

            for (int i = 0; i < additionsArray.length(); i++) {
                final String additionName = additionsArray.getJSONObject(i).getString("displayName");
                final String modifierId = additionsArray.getJSONObject(i).getString("id");

                if (!additionName.isEmpty()) additionTitle = additionName;

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
                            !additionDetailPriceWithoutVat.equals("") ? Double.parseDouble(additionDetailPriceWithoutVat) : 0));

                    Log.d(TAG, "addition detail name: " + additionDetailName);
                }

                /* do a null check on first item on the list and make it selected */
                OptionListItem firstItem = additionList.size() == 0 ? null : additionList.get(0);
                if (firstItem != null) firstItem.setSelected(true);

                String basePrice =  getPrices().split(",")[0];
                if (basePrice == null) basePrice = getPrices();

                basePrice = new DecimalFormat("0.##").format(Double.parseDouble(basePrice));

                additionOptions.add(new ModalListItem(getName(), basePrice, Constants.Type.ADDITION, getAdditionTitle(), additionList));
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
        return servingList;
    }

    public ArrayList<ModalListItem> getAdditions() {
        return additionOptions;
    }

    @Override
    public int compareTo(ItemListObject another) {
        return name.compareTo(another.getName());
    }
}
