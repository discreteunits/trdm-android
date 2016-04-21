package com.tastingroomdelmar.TastingRoomDelMar.parseUtils;

import com.parse.ParseObject;

/**
 * Created by Sean on 4/14/16.
 */
public class TopListObject implements Comparable<TopListObject> {
    private String name;
    private int sortOrder;
    private boolean selected = false;
    private String objectId;

    public TopListObject(String name) {
        this.name = name;
        this.sortOrder = -1; // always first
    }

    public TopListObject(ParseObject obj) {
        name = obj.getString("name");
        sortOrder = obj.getInt("sortOrder");
        objectId = obj.getObjectId();
    }

    public String getName() { return name; }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) { this.selected = selected; }

    public String getObjectId() { return objectId; }

    @Override
    public int compareTo(TopListObject compareObject) {
        int order = compareObject.getSortOrder();
        return this.sortOrder - order;
    }
}
