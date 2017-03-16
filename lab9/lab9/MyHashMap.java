package lab9;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by vip on 3/16/17.
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    HashSet<K> keys;
    LinkedList<Node>[] values;
    double maxLoadFactor;
    double size;

    private class Node {
        K key;
        V value;

        Node(K key1, V value1) {
            key = key1;
            value = value1;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    public MyHashMap() {
        keys = new HashSet<K>();
        values = new LinkedList[8];
        for (int i = 0; i < 8; i++) {
            values[i] = new LinkedList<Node>();
        }
        maxLoadFactor = 2;
        size = 0;
    }

    public MyHashMap(int initialSize) {
        keys = new HashSet<K>();
        values = new LinkedList[initialSize];
        for (int i = 0; i < initialSize; i++) {
            values[i] = new LinkedList<Node>();
        }
        maxLoadFactor = 2;
        size = 0;
    }

    public MyHashMap(int initialSize, double loadFactor) {
        keys = new HashSet<K>();
        values = new LinkedList[initialSize];
        for (int i = 0; i < initialSize; i++) {
            values[i] = new LinkedList<Node>();
        }
        maxLoadFactor = loadFactor;
        size = 0;
    }

    public void put(K key, V value) {
        if (size >= (double) values.length * maxLoadFactor) {
            resize();
        }
        if (!keys.contains(key)) {
            keys.add(key);
            Node poorthing = new Node(key, value);
            int hashnumber = poorthing.hashCode();
            hashnumber=Math.abs(hashnumber);
            int index = hashnumber % values.length;
            values[index].add(poorthing);
            size = size + 1;
        } else {
            int hashnumber = key.hashCode();
            int index = hashnumber % values.length;
            LinkedList<Node> row = values[index];
            for (Node item : row) {
                if (item.key.equals(key)) {
                    item.value = value;
                    break;
                }
            }
        }
    }

    public void resize() {
        int newsize = values.length * 2;
        LinkedList<Node>[] newvalues = new LinkedList[newsize];
        for (int i = 0; i < newsize; i++) {
            newvalues[i] = new LinkedList<Node>();
        }
        for (int i = 0; i < values.length; i++) {
            LinkedList<Node> row = values[i];
            for (Node item : row) {
                int hashnumber = item.hashCode();
                int index = hashnumber % newsize;
                newvalues[index].add(item);
            }
        }
        values = newvalues;
    }

    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    public void clear() {
        keys.clear();
        LinkedList<Node>[] newvalues = new LinkedList[values.length];
        for (int i = 0; i < values.length; i++) {
            newvalues[i] = new LinkedList<Node>();
        }
        values = newvalues;
        size = 0;
    }

    public int size() {
        return (int) size;
    }

    public V get(K key) {
        int index = Math.abs(key.hashCode());
        int realindex = index % values.length;
        LinkedList<Node> row = values[realindex];
        if (row.size() != 0) {
            for (Node item : row) {
                if (item.key.equals(key)) {
                    return item.value;
                }
            }
        }
        return null;
    }

    public Set<K> keySet() {
        return keys;
    }

    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        return keys.iterator();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }


}
