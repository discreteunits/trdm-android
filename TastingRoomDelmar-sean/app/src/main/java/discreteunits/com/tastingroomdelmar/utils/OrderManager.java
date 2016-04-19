package discreteunits.com.tastingroomdelmar.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sean on 4/17/16.
 */
public class OrderManager {
    private static final String TAG = OrderManager.class.getSimpleName();
    private static JSONObject obj;

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



}
