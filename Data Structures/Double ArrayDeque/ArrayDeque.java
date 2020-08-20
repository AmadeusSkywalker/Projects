public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = items.length / 2;
        nextLast = nextFirst + 1;
    }

    private void resize(int capacity) {
        /*
        Item[] a = (Item[]) new Object[capacity];
        System.arraycopy(items, nextFirst + 1, a, capacity - items.length + nextFirst + 1,
                items.length - nextFirst - 1);
        System.arraycopy(items, 0, a, 0, nextLast);
        nextFirst = capacity - items.length + nextFirst;
        items = a;
        */
        Item[] a = (Item[]) new Object[capacity];
        int first = (nextFirst + 1) % items.length;
        for (int index = 0; index < size; index++) {
            a[index] = items[first];
            first = (first + 1) % items.length;
        }
        nextFirst = capacity - 1;
        nextLast = size;
        items = a;
    }

    public void addFirst(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = x;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size = size + 1;
    }

    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = x;
        nextLast = (nextLast + 1 + items.length) % items.length;
        size = size + 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < items.length; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
        System.out.println();
    }

    public Item removeFirst() {
        if (size <= items.length / 4 && items.length >= 16) {
            resize(size * 2);
        }
        int index = (nextFirst + 1 + items.length) % items.length;
        Item result = items[index];
        if (result == null) {
            return null;
        }
        items[index] = null;
        nextFirst = index;
        size = size - 1;
        return result;
    }

    public Item removeLast() {
        if (size <= items.length / 4 && items.length >= 16) {
            resize(size * 2);
        }
        int index = (nextLast - 1 + items.length) % items.length;
        Item result = items[index];
        if (result == null) {
            return null;
        }
        items[index] = null;
        nextLast = index;
        size = size - 1;
        return result;
    }

    public Item get(int index) {
        if (index >= size) {
            return null;
        }
        return items[(nextFirst + index + 1) % items.length];
    }
}
