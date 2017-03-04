package db;

import db.TableItem;
import java.util.ArrayList;

/**
 * Created by ErichRathkamp on 2/27/17.
 */
public class TableItemCombiner {
    ArrayList<String> tableKeys;
    ArrayList<String> tableTypes;
    String operation;
    String colOne;
    String colTwo = null; //If colTwo is null, then don't use combiner method
    String resultName;
    Object thing2 = false;

    public TableItemCombiner(String combiner, ArrayList<String> keys, ArrayList<String> types) {
        tableKeys = keys;
        tableTypes = types;
        if (combiner.contains("+")) {
            operation = "+";
            cataloguer(combiner);
        } else if (combiner.contains("-")) {
            operation = "-";
            cataloguer(combiner);
        } else if (combiner.contains("*")) {
            operation = "*";
            cataloguer(combiner);
        } else if (combiner.contains("/")) {
            operation = "/";
            cataloguer(combiner);
        } else if (combiner.equals("")) {
            throw new RuntimeException("ERROR: Invalid combiner");
        } else {
            operation = "identity";
            if (tableKeys.contains(combiner)) {
                colOne = combiner;
            } else {
                throw new RuntimeException("ERROR: Column not in table");
            }

        }
    }

    private void cataloguer(String combiner) {
        int asInd = combiner.indexOf("[as]");
        resultName = combiner.substring(asInd + 4); //No spaces
        combiner = combiner.substring(0, asInd); //Removes the "as ..."
        int opInd = combiner.indexOf(operation); //Index of +, -, etc
        String thing = combiner.substring(opInd + 1); //gets the second name
        colOne = combiner.substring(0, opInd);
        if (!tableKeys.contains(colOne)) {
            throw new RuntimeException("ERROR: Column not in table");
        }
        if (tableKeys.contains(thing)) {
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

    public TableItem combiner(Row row) {
        TableItem item1 = row.get(tableKeys.indexOf(colOne));
        TableItem item2;

        if (colTwo != null) { //If there's a second row instead of a literal
            item2 = row.get(tableKeys.indexOf(colTwo));
        } else {
            item2 = new TableItem(thing2);
        }

        if (item1.type.equals("string") && !(item2.type.equals("string"))) {
            throw new RuntimeException("ERROR: Invalid comparison between non-string and String: " + item1);
        } else if (item2.type.equals("string") && !(item1.type.equals("string"))) {
            throw new RuntimeException("ERROR: Invalid comparison between non-string and String: " + item2);
        } else if (item1.type.equals("string") && !(operation.equals("+"))) {
            throw new RuntimeException("ERROR: Invalid operation: you may only add Strings");
        }
        if (item1.NaN || item2.NaN) {
            return new TableItem(null);
        }

        if (item1.type.equals("string")) { //Item 2 will always be String in this condition
            return new TableItem((String) item1.item + item2.item);
        } else if (item1.type.equals("int") && item2.type.equals("int")) {
            if (operation.equals("+")) {
                return new TableItem((Integer) item1.item + (Integer) item2.item);
            } else if (operation.equals("-")) {
                return new TableItem((Integer) item1.item - (Integer) item2.item);
            } else if (operation.equals("*")) {
                return new TableItem((Integer) item1.item * (Integer) item2.item);
            } else if (operation.equals("/")) {
                if ((Integer) item2.item == 0) { //If divide by 0 error
                    return new TableItem(null);
                } else {
                    return new TableItem((Integer) item1.item / (Integer) item2.item);
                }
            }

        } else if (item1.type.equals("float") && item2.type.equals("float")) {
            if (operation.equals("+")) {
                return new TableItem((Float) item1.item + (Float) item2.item);
            } else if (operation.equals("-")) {
                return new TableItem((Float) item1.item - (Float) item2.item);
            } else if (operation.equals("*")) {
                return new TableItem((Float) item1.item * (Float) item2.item);
            } else if (operation.equals("/")) {
                if (((Float) item2.item).equals(new Float(0))) { //If divide by 0 error
                    return new TableItem(null);
                } else {
                    return new TableItem((Float) item1.item / (Float) item2.item);
                }
            }

        } else if (item1.type.equals("float") && item2.type.equals("int")) {
            if (operation.equals("+")) {
                return new TableItem((Float) item1.item + (Integer) item2.item);
            } else if (operation.equals("-")) {
                return new TableItem((Float) item1.item - (Integer) item2.item);
            } else if (operation.equals("*")) {
                return new TableItem((Float) item1.item * (Integer) item2.item);
            } else if (operation.equals("/")) {
                if (((Integer) item2.item).equals(new Integer(0))) { //If divide by 0 error
                    return new TableItem(null);
                } else {
                    return new TableItem((Float) item1.item / (Integer) item2.item);
                }
            }

        } else if (item1.type.equals("int") && item2.type.equals("float")) {
            if (operation.equals("+")) {
                return new TableItem((Integer) item1.item + (Float) item2.item);
            } else if (operation.equals("-")) {
                return new TableItem((Integer) item1.item - (Float) item2.item);
            } else if (operation.equals("*")) {
                return new TableItem((Integer) item1.item * (Float) item2.item);
            } else if (operation.equals("/")) {
                if (((Float) item2.item).equals(new Float(0))) { //If divide by 0 error
                    return new TableItem(null);
                } else {
                    return new TableItem((Integer) item1.item / (Float) item2.item);
                }
            }
        }

        throw new RuntimeException("ERROR: Invalid combiner element types");
    }
}
