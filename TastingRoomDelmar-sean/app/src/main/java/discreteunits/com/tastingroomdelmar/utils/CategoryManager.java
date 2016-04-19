package discreteunits.com.tastingroomdelmar.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Sean on 4/1/16.
 */
public class CategoryManager {
    private static final String TAG = CategoryManager.class.getSimpleName();

    private static String verietal = "";

    private static ArrayList<String> list = new ArrayList<>();

    private static ArrayList<String> allList = new ArrayList<>();

    public static ArrayList getList() {
        return list;
    }

    public static void addToList(String categoryId) {
        list.add(categoryId);
    }

    public static void popFromList() {
        if (list.size() > 0)
            list.remove(list.size()-1);
        printCategory();
    }

    public static void printCategory() {
        for (String objectId : list) {
            Log.i(TAG, "Category: " + objectId);
        }
    }

    public static int getListSize() {
        return list.size();
    }

    public static boolean isInList(ArrayList<String> al) {
        Log.d(TAG, "------- objectId in Categories Array -------");
        for (String objectId : al) Log.d(TAG, objectId);
        Log.d(TAG, "--------------------------------------------");
        for (String objectId : list) {
            if (!al.contains(objectId)) return false;
        }

        return true;
    }

    public static String findVerietalId(JSONArray categories, final ArrayList<String> topList) {
        ArrayList<String> categoryList = new ArrayList<>();

        try {
            for (int i = 0; i < categories.length(); i++) {
                categoryList.add(categories.getJSONObject(i).getString("objectId"));
            }

            for (String c : categoryList) {
                if (!list.contains(c) && !verietal.equals("Full List") && !allList.contains(c)) {
                    return c;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "ERROR";
        }

        return "";
    }

    public static void addToAllList(String name) {
        if (!allList.contains(name)) allList.add(name);
    }

}
