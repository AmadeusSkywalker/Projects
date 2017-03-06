package db;

import java.util.ArrayList;

//Example: "x>=y" or "x>=4"
public class TableItemComparator {
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
        } else if (comparator.contains("<") && !(comparator.contains("<="))) {
            operation = "<";
            cataloguer(comparator);
        } else if (comparator.contains("<=")) {
            operation = "<=";
            cataloguer(comparator);
        } else if (comparator.contains(">=")) {
            operation = ">=";
            cataloguer(comparator);
        } else { //Make it initialize with empty string if null
            throw new RuntimeException("ERROR: Invalid comparator");
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
                throw new RuntimeException("ERROR: Tried to compare things that we don't handle");
            }

        }



    }

    public boolean compare(Row row) {
        TableItem item1 = row.get(tableKeys.indexOf(colOne));
        TableItem item2;
        if (!(colTwo == null)) {
            item2 = row.get(tableKeys.indexOf(colTwo)); //Second item is in row
        } else {
            item2 = new TableItem(thing2); //If second item is a literal
        }

        if ((item1.type.equals("string") && !item2.type.equals("string"))
                || (item2.type.equals("string") && !item1.type.equals("string"))) {
            throw new RuntimeException("ERROR: Cannot compare String with non-string");
        } else {
            if (item1.NOVALUE || item2.NOVALUE) {
                return false;
            }
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
        if (item1.NaN && item2.NaN) {
            return 0;
        } else if (item1.NaN) {
            return 1;
        } else if (item2.NaN) {
            return -1;
        } else {
            if (item1.type.equals("float") && item2.type.equals("float")) {
                if ((Float) item1.item > (Float) item2.item) {
                    return 1;
                } else if ((Float) item1.item < (Float) item2.item) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (item1.type.equals("float") && item2.type.equals("int")) {
                if ((Float) item1.item > (Integer) item2.item) {
                    return 1;
                } else if ((Float) item1.item < (Integer) item2.item) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (item1.type.equals("int") && item2.type.equals("float")) {
                if ((Integer) item1.item > (Float) item2.item) {
                    return 1;
                } else if ((Integer) item1.item < (Float) item2.item) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (item1.type.equals("int") && item2.type.equals("int")) {
                return (Integer) item1.item - (Integer) item2.item;
            } else if (item1.type.equals("string") && item2.type.equals("string")) {
                return ((String) item1.item).compareTo((String) item2.item);
            } else {
                throw new RuntimeException("ERROR: Tried to compare bad types");
            }

        }
    }
}
