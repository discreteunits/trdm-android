package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Sean on 2/14/16.
 */
public class ListObject implements Comparable<ListObject> {
    private ParseObject object;
    private int sortOrder;
    private String name;
    private String objectId;
    private String categoryId;
    private boolean skipToTier4;

    public ListObject (ParseObject obj, int sortOrder, String objectId, String categoryId, String name, boolean skipToTier4) {
        this.object = obj;
        this.sortOrder = sortOrder;
        this.name = name;
        this.objectId = objectId;
        this.categoryId = categoryId;
        this.skipToTier4 = skipToTier4;
    }

    public ParseObject getObject() { return object; }

    public String getName() {
        return name;
    }

    public String getId() { return objectId; }

    public String getCategoryId() { return categoryId; }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean skipToTier4() { return skipToTier4; }

    public ArrayList<String> getAllOptions() {
        return null;
    }




    @Override
    public int compareTo(ListObject compareObject) {
        int order = compareObject.getSortOrder();
        return this.sortOrder - order;
    }
}
