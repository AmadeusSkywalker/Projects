package db;

import java.util.Comparator;

/**
 * Created by vip on 2/19/17.
 */
public class TableItemComparator<T extends Comparable<T>> implements Comparator<T> {
    public int compare(T item1, T item2) {
        if (item1 == null && item2 == null) {
            return 0;
        } else if (item1 == null) {
            return 1;
        } else if (item2 == null) {
            return -1;
        } else {
            return item1.compareTo(item2);
        }
        }
    }
