package com.tastingroomdelmar.TastingRoomDelMar.utils;

import android.util.Log;

import java.util.ArrayList;

/* This is a singleton class that manages Tags
 * Only one instance should be alive across the app
 */
public class OIDManager {
    private static final String TAG = OIDManager.class.getSimpleName();

    private static ArrayList<String> list = new ArrayList<>();

    public static ArrayList getList() {
        return list;
    }

    public static void addToList(String objectID) {
        list.add(objectID);
    }

    public static void popFromList() {
        if (list.size() > 0)
            list.remove(list.size()-1);
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
}
