package utils;

import android.util.Log;

import java.util.ArrayList;

/* This is a singleton class that manages Tags
 * Only one instance should be alive across the app
 */
public class TagManager {
    private static final String TAG = TagManager.class.getSimpleName();

    private static ArrayList<String> list = new ArrayList<>();

    public static ArrayList getList() {
        return list;
    }

    public static void addToList(String tag) {
        list.add(tag);
    }

    public static void popFromList() {
        list.remove(list.size()-1);
        printTag();
    }

    public static void printTag() {
        for (String tag : list) {
            Log.i(TAG, "tag: " + tag);
        }
    }

    public static int getListSize() {
        return list.size();
    }

    public static boolean isInList(String tag){
        for(String t : list) {
            if (t.equals(tag)) return true;
        }

        return false;
    }
}
