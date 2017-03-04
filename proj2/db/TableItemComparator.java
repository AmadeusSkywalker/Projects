package db;

import java.util.ArrayList;

/**
 * Created by vip on 3/3/17.
 */
public class TableItemComparator {


    //Example: "x>=y" or "x>=4"
    ArrayList<String> tableKeys;
    ArrayList<String> tableTypes;
    String operation;
    String colOne;
    String colTwo = null; //Maybe make into an object?  Can then have second arg here
    Object thing2 = false; //Later do if it's boolean and false

    public TableItemComparator(String comparator, ArrayList<String> tableKeys, ArrayList<String> tableTypes) {
        this.tableTypes = tableTypes;
        this.tableKeys = tableKeys;
        if (comparator.contains("==")) {
            operation = "==";
            cataloguer(comparator);
        } else if (comparator.contains("!=")) {
            operation = "!=";
            cataloguer(comparator);
        } else if (comparator.contains(">") && !(comparator.contains(">="))) {
            operation = ">";
            cataloguer(comparator);
        } else if (comparator.contains("<") && !(comparator.contains(">="))) {
            operation = "<";
            cataloguer(comparator);
        } else if (comparator.contains("<=")) {
            operation = "<=";
            cataloguer(comparator);
        } else if (comparator.contains(">=")) {
            operation = ">=";
            cataloguer(comparator);
        } else if (!(comparator.equals(""))) { //Make it initialize with empty string if null
            throw new RuntimeException("Invalid comparator");
        } else {
            operation = "identity";
            colOne = comparator;
        }
    }

    private void cataloguer(String combiner) {
        int opInd = combiner.indexOf(operation);
        String thing; //The second item of the condition, may not be a column
        if (operation.length() == 1) {
            thing = combiner.substring(opInd + 1); //gets the second name
        } else {
            thing = combiner.substring(opInd + 2);
        }
        colOne = combiner.substring(0, opInd);

        if (tableKeys.contains(thing)) { //If not, then thing is either String, Float, or Int
            colTwo = thing;
        } else {
            if (thing.substring(0, 1).equals("'")) { //Type is string
                thing2 = thing.substring(1, thing.length() - 1);
            } else if (tableTypes.get(tableKeys.indexOf(colOne)).equals("float")) {
                thing2 = Float.valueOf(thing);
            } else if (tableTypes.get(tableKeys.indexOf(colOne)).equals("int")) {
                thing2 = Integer.valueOf(thing);
            } else {
                throw new RuntimeException("Tried to compare things that we don't handle");
            }

        }


    }

    public boolean compare(ArrayList<TableItem> row) {
        TableItem item1 = row.get(tableKeys.indexOf(colOne));
        TableItem item2;
        if (!(colTwo == null)) {
            item2 = row.get(tableKeys.indexOf(colTwo));
        } else {
            item2 = new TableItem(thing2);
        }

        if ((item1.type.equals("string") && !item2.type.equals("string"))
                || (item2.type.equals("string") && !item1.type.equals("string"))) {
            throw new RuntimeException("Cannot compare String with non-string");
        } else {
            int compareVal = compareHelper(item1, item2); //Should be the only necessary case
            return compareToConvert(compareVal);
        }
    }

    private boolean compareToConvert(int comparedInt) {
        if (operation.equals("==")) {
            if (comparedInt == 0) {
                return true;
            } else {
                return false;
            }
        } else if (operation.equals("!=")) {
            if (comparedInt == 0) {
                return false;
            } else {
                return true;
            }
        } else if (operation.equals(">")) {
            if (comparedInt > 0) {
                return true;
            } else {
                return false;
            }
        } else if (operation.equals("<")) {
            if (comparedInt < 0) {
                return true;
            } else {
                return false;
            }

        } else if (operation.equals("<=")) {
            if (comparedInt <= 0) {
                return true;
            } else {
                return false;
            }

        } else if (operation.equals(">=")) {
            if (comparedInt >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new RuntimeException("ERROR: Stored operation not valid");
        }
    }

    private int compareHelper(TableItem item1, TableItem item2) {
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


