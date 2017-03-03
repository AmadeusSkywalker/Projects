package db;

import db.TableItem;

/**
 * Created by ErichRathkamp on 2/27/17.
 */
public class TableItemCombiner {
    String operation;
    String colOne;
    String colTwo = null; //If colTwo is null, then don't use combiner method
    String resultName;

    public TableItemCombiner(String combiner) {
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
            throw new RuntimeException("Invalid combiner");
        } else {
            operation = "identity";
            colOne = combiner;
        }
    }

    private void cataloguer(String combiner) {
        int asInd = combiner.indexOf("[as]");
        resultName = combiner.substring(asInd + 4); //No spaces
        combiner = combiner.substring(0, asInd); //Removes the "as ..."
        int opInd = combiner.indexOf(operation);
        colTwo = combiner.substring(opInd + 1); //gets the second name
        colOne = combiner.substring(0, opInd);

    }

    public TableItem combiner(TableItem item1, TableItem item2) {
        if (item1.type.equals("string") && !(item2.type.equals("string"))) {
            throw new RuntimeException("Invalid comparison between non-string and String: " + item1);
        } else if (item2.type.equals("string") && !(item1.type.equals("string"))) {
            throw new RuntimeException("Invalid comparison between non-string and String: " + item2);
        } else if (item1.type.equals("string") && !(operation.equals("+"))) {
            throw new RuntimeException("Invalid operation: you may only add Strings");
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
