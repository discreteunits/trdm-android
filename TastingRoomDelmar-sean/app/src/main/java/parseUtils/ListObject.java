package parseUtils;

/**
 * Created by Sean on 2/14/16.
 */
public class ListObject implements Comparable<ListObject> {
    private int sortOrder;
    private String name;

    public ListObject (int sortOrder, String name) {
        this.sortOrder = sortOrder;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public int compareTo(ListObject compareObject) {
        int order = compareObject.getSortOrder();
        return this.sortOrder - order;
    }
}
