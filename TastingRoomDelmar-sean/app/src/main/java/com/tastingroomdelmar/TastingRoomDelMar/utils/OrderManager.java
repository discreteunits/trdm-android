package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import com.parse.ParseUser;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sean on 4/17/16.
 */
public class OrderManager {
    private static final String TAG = OrderManager.class.getSimpleName();

    private static ParseUser user;

    private static JSONObject topLevelObject;
    private static JSONObject dineinObject;
    private static JSONObject takeawayObject;
    private static JSONObject dineinBodyObject;
    private static JSONObject takeawayBodyObject;

    JSONArray ordersArray;
    JSONArray dineinOrderItems;
    JSONArray takeawayOrderItems;

    private static ArrayList<OrderListItem> orderListItems;

    private static OrderManager singleton;

    /* Setup all the structures needed */
    public OrderManager() {
        if (singleton == null) {
            orderListItems = new ArrayList<>();

            topLevelObject = new JSONObject();
            dineinObject = new JSONObject();
            takeawayObject = new JSONObject();
            dineinBodyObject = new JSONObject();
            takeawayBodyObject = new JSONObject();

            ordersArray = new JSONArray();
            dineinOrderItems = new JSONArray();
            takeawayOrderItems = new JSONArray();

            try {
                topLevelObject.put("userId", "");
                topLevelObject.put("orders", this.ordersArray);

                ordersArray.put(dineinObject);
                ordersArray.put(takeawayObject);

                dineinObject.put("body", dineinBodyObject);
                takeawayObject.put("body", takeawayBodyObject);

                dineinBodyObject.put("type", "delivery");
                dineinBodyObject.put("orderItems", dineinOrderItems);
                takeawayBodyObject.put("type", "takeaway");
                takeawayBodyObject.put("orderItems", takeawayOrderItems);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            singleton = this;
        }
    }

    public static OrderManager getSingleton() {
        return singleton;
    }

    public OrderManager(JSONObject obj) {
        topLevelObject = obj;
    }

    public void printOrder() {
        try {
            Log.d(TAG, topLevelObject.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Set user and put that in the order JSONObject.
     *
     * @throws JSONException if user id was not found
     */
    public void setUser(ParseUser user) throws JSONException {

    }

    public void addToDineIn(JSONObject dineInObj) {
        dineinOrderItems.put(dineInObj);
    }

    public void addToTakeAway(JSONObject takeAwayObj) {
        takeawayOrderItems.put(takeAwayObj);
    }

    public void setCommons(String checkoutMethod, String table, String tipPercent, String note)
    throws JSONException {
        for(int i = 0; i < ordersArray.length(); i++) {
            JSONObject orderTypeItem = ordersArray.getJSONObject(i);
            orderTypeItem.put("checkoutMethod", checkoutMethod);
            orderTypeItem.put("table", table);
            orderTypeItem.put("tipPercent", tipPercent);
            orderTypeItem.getJSONObject("body").put("note", note);
        }
    }

    public void addToOrderList(OrderListItem item) {
        orderListItems.add(item);
    }

    public ArrayList<OrderListItem> getOrderListItems() {
        return orderListItems;
    }
}
