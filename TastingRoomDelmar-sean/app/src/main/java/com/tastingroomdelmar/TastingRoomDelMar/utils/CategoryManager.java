package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Sean on 4/1/16.
 */
public class CategoryManager {
    private static final String TAG = CategoryManager.class.getSimpleName();

    private static String verietal = "";

    private static String dinein;
    private static String takeaway;
    private static String events;

    private static ParseObject dineinObject;
    private static ParseObject takeawayObject;
    private static ParseObject eventsObject;

    private static ArrayList<String> list = new ArrayList<>();

    private static ArrayList<String> allList = new ArrayList<>();

    private static ArrayList<String> nameList = new ArrayList<>();

    private static ArrayList<ParseObject> objectList = new ArrayList<>();

    private static boolean isDinein = false;
    private static boolean isWine = false;
    private static boolean isBeer = false;

    public static ArrayList getList() {
        return list;
    }

    public static void addToList(String categoryId, String name) {
        list.add(categoryId); printCategory();
        nameList.add(name);
    }

    public static void addToObjectList(ParseObject obj) {
        objectList.add(obj);
    }

    public static void popFromList() {
        if (list.size() > 0)
            list.remove(list.size()-1);

        if (objectList.size() > 0)
            objectList.remove(objectList.size()-1);

        if (nameList.size() > 0)
            nameList.remove(nameList.size()-1);

        printCategory();
    }

    public static void popAll() {
        list.clear();
        list = new ArrayList<>();
        objectList.clear();
        objectList = new ArrayList<>();

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

    public static void setDinein(boolean dinein) {
        isDinein = dinein;
    }
    public static boolean isDinein() { return isDinein; }

    public static void setIsWine(boolean wine) { isWine = wine; }
    public static boolean isWine() { return isWine; }

    public static void setIsBeer(boolean beer) { isBeer = beer; }
    public static boolean isBeer() { return isBeer; }

    public static void setCategories(ParseObject dineinObj, String d, ParseObject takeawayObj, String t, ParseObject eventsObj, String e) {
        dinein = d;
        takeaway = t;
        events = e;
        dineinObject = dineinObj;
        takeawayObject = takeawayObj;
        eventsObject = eventsObj;
    }

    public static String getDineinCategoryId() { return dinein; }
    public static ParseObject getDineineObject() { return dineinObject; }

    public static String getTakeawayCategoryId() {
        return takeaway;
    }
    public static ParseObject getTakeawayObject() { return takeawayObject; }

    public static String getEventsCategoryId() {
        return events;
    }
    public static ParseObject getEventsObject() { return eventsObject; }

    public static ArrayList<ParseObject> getObjectList() {
        return objectList;
    }

    public static ArrayList<String> getNameList() { return nameList; }
}
