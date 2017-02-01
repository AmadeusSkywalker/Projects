public class LinkedListDeque<Item> {

    private class StuffNode {
        private Item item;
        private StuffNode next;
        private StuffNode prev;

        public StuffNode(Item c, StuffNode a, StuffNode b) {
            item = c;
            next = a;
            prev = b;
        }

        public Item getItem(StuffNode x){
            return x.item;
        }

        public StuffNode getNext(StuffNode x){
            return x.next;
        }

        public StuffNode getPrev(StuffNode x){
            return x.prev;
        }
    }

    private StuffNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new StuffNode(null, sentinel, sentinel);
        size = 0;
    }

    public void addFirst(Item x) {
        if (size == 0) {
            sentinel.next = new StuffNode(x, sentinel, sentinel);
            sentinel.prev = sentinel.next;
            size = size + 1;
        } else {
            sentinel.next = new StuffNode(x, sentinel.next, sentinel);
            sentinel.next.next.prev = sentinel.next;
            size += 1;
        }
    }

    public void addLast(Item x) {
        if (size == 0) {
            sentinel.next = new StuffNode(x, sentinel, sentinel);
            sentinel.prev = sentinel.next;
            size = size + 1;
        } else {
            sentinel.prev = new StuffNode(x, sentinel, sentinel.prev);
            sentinel.prev.prev.next = sentinel.prev;
            size += 1;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int index = 1;
        StuffNode pointer = sentinel.next;
        while (index <= size) {
            System.out.print(pointer.item + " ");
            index = index + 1;
            pointer = pointer.next;
        }
        System.out.println();
    }

    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        StuffNode pointer = sentinel.next;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size = size - 1;
        return pointer.item;
    }

    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        StuffNode pointer = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size = size - 1;
        return pointer.item;
    }

    public Item get(int x) {
        StuffNode pointer = sentinel.next;
        if (x >= size) {
            return null;
        }
        int index = x;
        while (index > 0) {
            pointer = pointer.next;
            index = index - 1;
        }
        return pointer.item;
    }

    public Item getRecursive(int x) {
        if (x >= size) {
            return null;
        }
        return recursehelp(x, sentinel.next);
    }

    private Item recursehelp(int x, StuffNode m) {
        if (x == 0) {
            return m.item;
        }
        return recursehelp(x - 1, m.next);
    }
}
