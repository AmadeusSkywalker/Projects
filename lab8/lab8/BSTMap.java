package lab8;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by vip on 3/9/17.
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;
    private int size;

    private class Node {
        private K key;
        private V value;
        private Node left, right;
        private Node parent;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }


    public BSTMap() {
        size = 0;
        root = null;
    }

    @Override
    public void clear() {
        clearhelper(root);
        root = null;
        size = 0;
    }

    public void clearhelper(Node x) {
        if (x.left != null) {
            clearhelper(x.left);
            x.left = null;
        }
        if (x.right != null) {
            clearhelper(x.right);
            x.right = null;
        }
        x.key = null;
        x.value = null;
    }

    @Override
    public boolean containsKey(K key1) {
        return containsKeyhelper(key1, root);
    }

    private boolean containsKeyhelper(K key1, Node x) {
        if (x != null) {
            if (x.key.equals(key1)) {
                return true;
            } else {
                boolean left = containsKeyhelper(key1, x.left);
                boolean right = containsKeyhelper(key1, x.right);
                return left || right;
            }
        }
        return false;
    }

    @Override
    public V get(K key1) {
        return gethelper(key1, root);
    }

    private V gethelper(K key1, Node x) {
        if (x == null) {
            return null;
        }
        int cmp = key1.compareTo(x.key);
        if (cmp < 0) {
            return gethelper(key1, x.left);
        }
        if (cmp > 0) {
            return gethelper(key1, x.right);
        }
        return x.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (this.root == null) {
            root = new Node(key, value);
        } else {
            puthelper(key, value, root);
        }
        size = size + 1;
    }

    public void puthelper(K key1, V value1, Node x) {
        if (key1.compareTo(x.key) > 0) {
            if (x.right == null) {
                x.right = new Node(key1, value1);
            } else {
                puthelper(key1, value1, x.right);
            }
        } else {
            if (x.left == null) {
                x.left = new Node(key1, value1);
            } else {
                puthelper(key1, value1, x.left);
            }
        }
    }


    public void printInOrder() {
        printInOrder(this.root);
    }

    private void printInOrder(Node x) {
        if (x.left == null && x.right == null) {
            System.out.print(x.value + ", ");
        } else {
            printInOrder(x.left);
            System.out.println(x.value + ", ");
            printInOrder(x.right);
        }
    }

    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }
}
