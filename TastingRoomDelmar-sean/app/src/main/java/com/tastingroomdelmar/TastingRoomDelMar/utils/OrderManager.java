package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import com.parse.ParseUser;
import com.tastingroomdelmar.TastingRoomDelMar.parseUtils.OrderListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    double subTotalPrice;
    double taxRate;
    double taxPrice;
    double totalPrice;

    static int orderCount;

    static int numDinein = 0;
    static int numTakeaway = 0;


    private static ArrayList<OrderListItem> orderListItems;

    public interface OrderCountListener {
        void onOrderCountChanged(int count);
    }

    ArrayList<OrderCountListener> orderCountListenerArray;

    public void setOrderCountListener(OrderCountListener listener) { orderCountListenerArray.add(listener); }

    private static OrderManager singleton;

    /* Setup all the structures needed */
    public OrderManager() {
        if (singleton == null) {
            orderCount = 0;
            orderCountListenerArray = new ArrayList<>();

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

                //ordersArray.put(dineinObject);
                //ordersArray.put(takeawayObject);

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

            for (OrderCountListener ocl : orderCountListenerArray)
                ocl.onOrderCountChanged(orderCount);
        }
    }

    public static OrderManager getSingleton() {
        if (singleton == null) singleton = new OrderManager();

        return singleton;
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
        topLevelObject.put("userId", user.getObjectId());
    }

    public void addToDineIn(boolean isParent, JSONObject dineInObj) {
        dineinOrderItems.put(dineInObj);

        numDinein++;

        if (!isParent) orderCount++;

        for (OrderCountListener ocl : orderCountListenerArray)
            ocl.onOrderCountChanged(orderCount);
    }

    public void addToTakeAway(boolean isParent, JSONObject takeAwayObj) {
        takeawayOrderItems.put(takeAwayObj);

        numTakeaway++;

        if (!isParent) orderCount++;

        for (OrderCountListener ocl : orderCountListenerArray)
            ocl.onOrderCountChanged(orderCount);
    }

    Constants.CheckoutType checkoutType;
    String table;
    double tipPercent;
    String note;
    public void saveCommons(Constants.CheckoutType checkoutType, String table, double tipPercent, String note) {
        this.checkoutType = checkoutType;
        this.table = table;
        this.tipPercent = tipPercent;
        this.note = note;
    }

    private void setCommons(Constants.CheckoutType checkoutType, String table, double tipPercent, String note)
    throws JSONException {
        String checkoutMethod;
        if (checkoutType == Constants.CheckoutType.STRIPE) checkoutMethod = "stripe";
        else checkoutMethod = "server";

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

    public void addToSubTotal(double price) {
        subTotalPrice += price;
        if (subTotalPrice < 0) subTotalPrice = 0;
    }

    public double getSubTotalPrice() { return subTotalPrice; }

    public void addToTax(double taxPrice) {
        this.taxPrice += taxPrice;
        if (this.taxPrice < 0) this.taxPrice = 0;
    }

    public double getTaxPrice() { return taxPrice; }

    public Map<String, Object> getFinalizedOrderObject() {

        if (numDinein > 0) ordersArray.put(dineinObject);

        if (numTakeaway > 0) ordersArray.put(takeawayObject);

        try {
            setCommons(checkoutType, table, tipPercent, note);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> finalHashMap = null;
        try {
            finalHashMap = jsonToMap(topLevelObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalHashMap;
    }

    public int getOrderCount() {
        return orderCount;
    }

    private JSONArray removeFromJSONArray(JSONArray original, int index) {
        JSONArray output = new JSONArray();
        int len = original.length();
        for (int i = 0; i < len; i++)   {
            if (i != index) {
                try {
                    output.put(original.get(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return output;
    }

    public void removeItem(boolean isDineIn, boolean isChoiceItem, String id) {
        if (isDineIn) {
            for (int i = 0; i < dineinOrderItems.length(); i++) {
                try {
                    String objectId = dineinOrderItems.getJSONObject(i).getString("objectId");
                    if (objectId.equals(id)) {
                        dineinOrderItems = removeFromJSONArray(dineinOrderItems, i);
                        orderCount--;
                        numDinein--;

                        if (isChoiceItem) {
                            dineinOrderItems = removeFromJSONArray(dineinOrderItems, i); // i -> next index because it's already one size smaller
                            numDinein--;
                        }

                        dineinBodyObject.put("orderItems", dineinOrderItems);

                        for (OrderCountListener ocl : orderCountListenerArray)
                            ocl.onOrderCountChanged(orderCount);

                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < takeawayOrderItems.length(); i++) {
                try {
                    String objectId = takeawayOrderItems.getJSONObject(i).getString("objectId");
                    if (objectId.equals(id)) {
                        takeawayOrderItems = removeFromJSONArray(takeawayOrderItems, i);
                        orderCount--;
                        numTakeaway--;

                        if (isChoiceItem) {
                            takeawayOrderItems = removeFromJSONArray(takeawayOrderItems, i); // i -> next index because it's already one size smaller
                            numTakeaway--;
                        }

                        takeawayBodyObject.put("orderItems", takeawayOrderItems);

                        for (OrderCountListener ocl : orderCountListenerArray)
                            ocl.onOrderCountChanged(orderCount);

                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        printOrder();
    }

    public static void clearOrders() {
        singleton = null; // clear it up
        singleton = new OrderManager();
    }



    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
