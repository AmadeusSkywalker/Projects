package betterDB;

/**
 * Created by ErichRathkamp on 3/1/17.
 */
public class TableItem {
    String type;
    Object item;
    boolean NaN = false;
    boolean NOVALUE = false;

    TableItem(Object item) {
        if (item == null) { //TODO check if this works
            NaN = true;
        } else if (item instanceof String) {
            type = "string";
        } else if (item instanceof Float) {
            type = "float";
        } else if (item instanceof Integer) {
            type = "int";
        } else {
            throw new RuntimeException("Table only handles Strings, Floats, and Integers"); //TODO catch
        }
        this.item = item;
    }

    @Override
    public boolean equals(Object comparedItem) {
        if (comparedItem instanceof TableItem) {
            TableItem compItem = (TableItem) comparedItem;
            if (this.NaN) {
                return compItem.NaN;
            } else if (this.NOVALUE) {
                return compItem.NOVALUE;
            } else if (compItem.NaN) {
                return false;
            } else if (compItem.NOVALUE) {
                return false;
            }
            return this.item.equals(compItem.item);
        }
        return false;
    }
}
