package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import java.util.ArrayList;

/* This is a singleton class that manages Tags
 * Only one instance should be alive across the app
 */
public class OIDManager {
    private static final String TAG = OIDManager.class.getSimpleName();

    private static ArrayList<String> list = new ArrayList<>();

    private static String dinein;
    private static String takeaway;
    private static String events;

    public static ArrayList getList() {
        return list;
    }

    public static void addToList(String objectID) {
        list.add(objectID);
        printObjectId();
    }

    public static void popFromList() {
        if (list.size() > 0)
            list.remove(list.size()-1);
        printObjectId();
    }

    public static void popAll() {
        list.clear();
        list = new ArrayList<>();

        printObjectId();
    }

    public static void printObjectId() {
        for (String ObjectId : list) {
            Log.i(TAG, "objectId: " + ObjectId);
        }
    }

    public static int getListSize() {
        return list.size();
    }

    public static boolean isInList(String objectId){
        for(String id : list) {
            if (id.equals(objectId)) return true;
        }

        return false;
    }

    public static void setOIDs(String d, String t, String e) {
        dinein = d;
        takeaway = t;
        events = e;
    }

    public static String getDineinOID() {
        return dinein;
    }

    public static String getTakeawayOID() {
        return takeaway;
    }

    public static String getEventsOID() {
        return events;
    }
}
