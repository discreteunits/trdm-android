package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sean on 4/17/16.
 */
public class OrderManager {
    private static final String TAG = OrderManager.class.getSimpleName();
    private static JSONObject obj;
    private ParseUser user;
    public OrderManager(JSONObject obj) {
        this.obj = obj;
    }

    public void printOrder() {
        try {
            Log.d(TAG, obj.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUser(ParseUser user) { this.user = user; }



}
