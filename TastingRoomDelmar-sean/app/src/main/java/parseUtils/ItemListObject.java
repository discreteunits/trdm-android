package parseUtils;

import java.util.Comparator;

/**
 * Created by Sean on 2/14/16.
 */
public class ItemListObject implements Comparable<ItemListObject> {
    private String name;
    private String altName;
    private String tag;

    public ItemListObject(String tag, String name, String altName) {
        this.altName = altName;
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getTag() { return tag; }

    public String getAltName() {
        return altName;
    }

    @Override
    public int compareTo(ItemListObject another) {
        return name.compareTo(another.getName());
    }
}
