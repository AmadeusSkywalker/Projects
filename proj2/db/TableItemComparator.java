package db;

import java.util.Comparator;

/**
 * Created by vip on 2/19/17.
 */
public class TableItemComparator implements Comparator {

    public TableItemComparator() {

    }
    public int compare(Object item1, Object item2) {
        if (item1 == null && item2 == null) {
            return 0;
        } else if (item1 == null) {
            return 1;
        } else if (item2 == null) {
            return -1;
        } else {
            return ((Comparable) item1).compareTo(item2); //Item1 and Item2 always Comparable
        }
        }
    }
