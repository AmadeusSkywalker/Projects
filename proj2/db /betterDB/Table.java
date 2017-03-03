package betterDB;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ErichRathkamp on 3/1/17.
 */
public class Table {
    ArrayList<String> colHeaders;
    ArrayList<String> colNames;
    ArrayList<String> colTypes;
    ArrayList<Row> rows;
    String name;
    int numRows;
    int numCols;

    public Table(String tableName, ArrayList<String> names, ArrayList<String> types) {
        //The table constructor creates a first row and add this row to the rows variable
        HashSet<String> duplicates = new HashSet<>();
        for (String name : names) {
            duplicates.add(name);
        }
        if (duplicates.size() != names.size()) {
            throw new RuntimeException("ERROR: Duplicate column name");
        }

        name = tableName;
        colNames = names;
        colTypes = types;
        rows = new ArrayList<Row>();
        ArrayList<String> headers = new ArrayList<>();
        if (names.size() != types.size()) {
            throw new RuntimeException("ERROR: Names and types of differing length"); //TODO catch
        }
        for (int i = 0; i < names.size(); i++) {
            headers.add(names.get(i) + " " + types.get(i));
        }
        colHeaders = headers;
        numCols = names.size();
        numRows = 0;
    }

    public static Table join(String name, Table t1, Table t2) {
        ArrayList<String> t1names = t1.colNames;
        ArrayList<String> t2names = t2.colNames;
        ArrayList<String> t1types = t1.colTypes;
        ArrayList<String> t2types = t2.colTypes;
        ArrayList<String> samekeys = new ArrayList<>();
        ArrayList<String> sameTypes = new ArrayList<>();
        ArrayList<String> allkeys = new ArrayList<>();
        ArrayList<String> alltypes = new ArrayList<>();

        for (int i = 0; i < t1names.size(); i++) {
            if (t2names.contains(t1names.get(i))) {
                samekeys.add(t1names.get(i)); //The names and types stay parallel
                sameTypes.add(t1types.get(i));
            }
        }

        //Catalogues every name and type in t1
        for (int i = 0; i < t1names.size(); i++) {
            allkeys.add(t1names.get(i));
            alltypes.add(t1types.get(i));
        }

        //Catalogues every name and type in t2
        for (int i = 0; i < t2names.size(); i++) {
            allkeys.add(t2names.get(i));
            alltypes.add(t2types.get(i));
        }

        if (samekeys.size() == 0) {
            return cartesianJoin(name, t1, t2, allkeys, alltypes);
        } else {
            return innerJoin(name, t1, t2, samekeys, sameTypes);
        }
    }

    private static Table cartesianJoin(String name, Table t1, Table t2, ArrayList<String> names,
                                       ArrayList<String> types) {
        Table joined = new Table(name, names, types);

        //for each new row, just add the previous rows together, and add the new row to the new table
        for (int i = 0; i < t1.numRows; i++) {
            Row x = t1.rows.get(i);

            for (int k = 0; k < t2.numRows; k++) {
                Row y = t2.rows.get(k);
                ArrayList<TableItem> bigBody = new ArrayList<>();
                bigBody.addAll(x.body);
                bigBody.addAll(y.body);
                Row xy = new Row(bigBody);
                joined.addRow(xy);
            }
        }
        return joined;
    }

    private static Table innerJoin(String name, Table t1, Table t2, ArrayList<String> samekeys,
                                   ArrayList<String> sametypes) {
        //Below line makes a new table with the correct arrangement of headers
        Table result = innerjoinhelper(name, t1, t2, samekeys, sametypes);

        for (int i = 0; i < t1.numRows; i++) { //For each row in table 1
            for (int j = 0; j < t2.numRows; j++) { //For each row in table 2
                int matchedItems = 0;
                ArrayList<TableItem> newRow = new ArrayList<>();
                for (String key : samekeys) { //Need to know what column we're comparing items in
                    //Below lines compares elements in columns in a gross way
                    TableItem item1 = t1.rows.get(i).get(t1.colNames.indexOf(key));
                    TableItem item2 = t2.rows.get(j).get(t2.colNames.indexOf(key));
                    if (item1.equals(item2)) { //TODO test equals method
                        // == comparison only works with integers up some number in the hundreds
                        matchedItems += 1;
                        newRow.add(item1);
                    }
                }
                if (matchedItems == samekeys.size()) {
                    for (String colname : t1.colNames) {
                        if (!(samekeys.contains(colname))) {
                            newRow.add(t1.rows.get(i).get(t1.colNames.indexOf(colname)));
                        }
                    }
                    for (String colname : t2.colNames) {
                        if (!(samekeys.contains(colname))) {
                            newRow.add(t2.rows.get(j).get(t2.colNames.indexOf(colname)));
                        }
                    }
                    result.addRow(new Row(newRow));
                }

            }

        }
        return result;
    }

    private static Table innerjoinhelper(String name, Table t1, Table t2, ArrayList<String> samekeys,
                                         ArrayList<String> sametypes) {
        ArrayList<String> unsharedNames = new ArrayList<>();
        ArrayList<String> unsharedTypes = new ArrayList<>();

        for (int i = 0; i < t1.colNames.size(); i++) { //Adds all non-shared columns from t1
            if (!(samekeys.contains(t1.colNames.get(i)))) {
                unsharedNames.add(t1.colNames.get(i));
                unsharedTypes.add(t1.colTypes.get(i));
            }
        }
        for (int i = 0; i < t1.colNames.size(); i++) { //Adds all non-shared columns from t2
            if (!samekeys.contains(t2.colNames.get(i))) {
                unsharedNames.add(t2.colNames.get(i));
                unsharedTypes.add(t2.colTypes.get(i));
            }
        }

        ArrayList<String> totalNames = new ArrayList<>();
        ArrayList<String> totalTypes = new ArrayList<>();
        totalNames.addAll(samekeys);
        totalNames.addAll(unsharedNames);
        totalTypes.addAll(sametypes);
        totalTypes.addAll(unsharedTypes);

        return new Table(name, totalNames, totalTypes);

    }

    public ArrayList<String> getColTypes() {
        return colTypes;
    }

    public int getNumRows() {
        return numRows;
    }

    public String addRow(Row x) {
        for (int i = 0; i < x.body.size(); i++) {
            if (!(x.get(i).type.equals(colTypes.get(i)))) {
                return "ERROR: Type of row item not match table";
            }
        }
        rows.add(x);
        numRows += 1;
        return "";
    }

    public void addRow(ArrayList<TableItem> x) {
        Row newRow = new Row(x);
        addRow(newRow);
    }

    public String printtable() {
        String result = "";

        for (String header : colHeaders) {
            result = result + header + ",";
        }
        //Cuts the last comma
        result = result.substring(0, result.length() - 1);
        result += '\n';

        for (Row row : rows) {
            //printing the table row by row
            String currentRow = "";
            int index = 0;

            ArrayList<TableItem> toBePrinted = row.body;
            for (TableItem element : toBePrinted) {  //TODO put NOVALUE and NaN representations in
                //in each row, add the string representation of the TableItem to the result
                if (element.type.equals("string")) {
                    currentRow = currentRow + "'" + element.item.toString() + "'" + ",";
                } else if (element.type.equals("int")) {
                    currentRow = currentRow + element.item.toString() + ",";
                } else if (element.type.equals("float")) {
                    currentRow = currentRow + String.format(java.util.Locale.US, "%.3f", (Float) element.item) + ",";
                } else {
                    return "ERROR: Incorrect type in table";
                }
                //Cuts the last comma of a row
                index = index + 1;
                if (index == toBePrinted.size()) {
                    currentRow = currentRow.substring(0, currentRow.length() - 1);
                }


            }
            result = result + currentRow + '\n';
        }
        return result;
    }

}


