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

    double subTotalPrice;
    double taxRate;
    double taxPrice;
    double totalPrice;

    static int orderCount;

    private static ArrayList<OrderListItem> orderListItems;

    public interface OrderCountListener {
        void onOrderCountChanged(int count);
    }

    OrderCountListener orderCountListener;

    public void setOrderCountListener(OrderCountListener listener) { orderCountListener = listener; }

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

        if (!isParent) orderCount++;

        if (orderCountListener != null)
            orderCountListener.onOrderCountChanged(orderCount);
    }

    public void addToTakeAway(boolean isParent, JSONObject takeAwayObj) {
        takeawayOrderItems.put(takeAwayObj);

        if (!isParent) orderCount++;

        if (orderCountListener != null)
            orderCountListener.onOrderCountChanged(orderCount);
    }

    public void setCommons(Constants.CheckoutType checkoutType, String table, double tipPercent, String note)
    throws JSONException {
        String checkoutMethod;
        if (checkoutType == Constants.CheckoutType.STRIPE) checkoutMethod = "stripe";
        else checkoutMethod = "server";

        for(int i = 0; i < ordersArray.length(); i++) {
            JSONObject orderTypeItem = ordersArray.getJSONObject(i);
            orderTypeItem.put("checkoutMethod", checkoutMethod);
            orderTypeItem.put("table", Integer.parseInt(table));
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
    }

    public double getSubTotalPrice() { return subTotalPrice; }

    public void addToTax(double taxPrice) {
        this.taxPrice += taxPrice;
    }

    public double getTaxPrice() { return taxPrice; }

    public JSONObject getFinalizedOrderObject() {
        return topLevelObject;
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

                        if (isChoiceItem)
                            dineinOrderItems = removeFromJSONArray(dineinOrderItems, i); // i -> next index because it's already one size smaller

                        dineinBodyObject.put("orderItems", dineinOrderItems);
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

                        if (isChoiceItem)
                            takeawayOrderItems = removeFromJSONArray(takeawayOrderItems, i); // i -> next index because it's already one size smaller

                        takeawayBodyObject.put("orderItems", takeawayOrderItems);
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
}
