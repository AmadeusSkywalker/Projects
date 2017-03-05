package db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ErichRathkamp on 3/1/17.
 */
public class Database {
    private HashMap<String, Table> database;

    public Database() {
        database = new HashMap<String, Table>();
    }

    public HashMap<String, Table> getDatabase() {
        return database;
    }

    public void addTable(Table t1) {
        getDatabase().put(t1.name, t1);
    }

    public String createtable(String name, ArrayList<String> colnames, ArrayList<String> types) {
        if (database.containsKey(name)) {
            return "ERROR: Table already existed";
        }
        if (types.size() == 0 || colnames.size() == 0) {
            return "ERROR: Empty table";
        }
        if (types.size() != colnames.size()) {
            return "ERROR: Names and types size does not match!";
        }
        Table element = new Table(name, colnames, types);
        database.put(name, element); //put the newly created table in the database
        return "";
    }

    public String print(String tablename) {
        if (database.containsKey(tablename)) {
            Table changed = database.get(tablename);
            return changed.printtable();
        } else {
            return "ERROR: No such table!";
        }
    }

    public String droptable(String name) {
        if (database.containsKey(name)) {
            database.remove(name);
            return "";
        } else {
            return "ERROR: No such table.";
        }
    }

    public String load(String name,Database x) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(name + ".tbl"));
        String result = Loadhelp.load(reader, name,x);
        return result;
    }
    /*
    public String load(BufferedReader reader, String name) throws IOException {
        String firstLine = reader.readLine();
        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<String> columnTypes = new ArrayList<>();
        boolean isend = false;
        while (!isend) {
            int firstIndex = firstLine.indexOf(" ");
            int secondIndex = firstLine.indexOf(",");
            if (secondIndex == -1) {
                secondIndex = firstLine.length();
                isend = true;
            }
            String colName = firstLine.substring(0, firstIndex);
            String colType = firstLine.substring(firstIndex + 1, secondIndex);
            columnNames.add(colName);
            columnTypes.add(colType);
            if (!isend) {
                firstLine = firstLine.substring(secondIndex + 1);
            }
        }
        createtable(name, columnNames, columnTypes);
        String nextLine = reader.readLine();
        boolean isend2 = false;
        while (nextLine != null) { //runs per line
            int index = 0;
            ArrayList<TableItem> newRow = new ArrayList<>();
            while (!isend2) { //categorizes items inside each line
                int commaIndex = nextLine.indexOf(",");
                if (commaIndex == -1) {
                    commaIndex = nextLine.length();
                    isend2 = true;
                }
                String firstItem = nextLine.substring(0, commaIndex);
                if (columnTypes.get(index).equals("string")) {
                    if (firstItem.charAt(0) != '\''
                            || firstItem.charAt(firstItem.length() - 1) != '\'') {
                        return "ERROR: Incorrect String format";
                    }
                    firstItem = firstItem.substring(1, firstItem.length() - 1);
                    TableItem newItem = new TableItem(firstItem);
                    if (firstItem.equals("NaN")) {
                        return "ERROR: String cannot be NaN";
                    } else if (firstItem.equals("NOVALUE")) {
                        newItem.NOVALUE = true;
                        newItem.item = "";
                    }
                    newRow.add(newItem);
                } else if (columnTypes.get(index).equals("float")) {
                    TableItem newItem = new TableItem(Float.valueOf(firstItem));
                    if (firstItem.equals("NaN")) {
                        newItem.NaN = true;
                    } else if (firstItem.equals("NOVALUE")) {
                        newItem.NOVALUE = true;
                        newItem.item = new Float(0.0);
                    }
                    newRow.add(newItem);
                } else if (columnTypes.get(index).equals("int")) {
                    TableItem newItem = new TableItem(Integer.valueOf(firstItem));
                    if (firstItem.equals("NaN")) {
                        return "ERROR: String cannot be NaN";
                    } else if (firstItem.equals("NOVALUE")) {
                        newItem.NOVALUE = true;
                        newItem.item = "";
                    }
                    newRow.add(newItem);
                } else {
                    return "ERROR: Incorrect loaded col type";
                }
                index++;
                if (!isend2) {
                    nextLine = nextLine.substring(commaIndex + 1);
                }
            }
            isend2 = false;
            Row realNewRow = new Row(newRow);
            insertInto(name, realNewRow);
            nextLine = reader.readLine();
        }
        return "";
    }
    */

        public String store (String name){
            try {
                File file = new File(name + ".tbl"); // "./" if filepath doesn't work
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                Table currTable = database.get(name);
                ArrayList<String> headNames = currTable.colNames;
                ArrayList<String> headTypes = currTable.colTypes;

                for (int i = 0; i < headNames.size(); i++) {
                    writer.write(headNames.get(i) + " ");
                    writer.write(headTypes.get(i));
                    if (i != headNames.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                int numCols = currTable.numCols;
                ArrayList<Row> rowList = currTable.rows;
                for (int i = 0; i < currTable.numRows; i++) { //For every row
                    for (int j = 0; j < rowList.get(i).body.size(); j++) { //For every item in row
                        TableItem tItem = rowList.get(i).body.get(j);
                        if (tItem.type.equals("int") || tItem.type.equals("float")) {
                            if (tItem.NaN) {
                                writer.write("NaN");
                            } else if (tItem.NOVALUE) {
                                writer.write("NOVALUE");
                            } else {
                                writer.write(tItem.item.toString());
                            }
                        } else if (tItem.type.equals("string")) {
                            if (tItem.NOVALUE) {
                                writer.write("NOVALUE");
                            } else {
                                writer.write("'" + (String) tItem.item + "'");
                            }
                        }
                        if (!(j == rowList.get(i).body.size() - 1)) { //TODO fix this
                            writer.write(",");
                        }
                    }
                    if (i < currTable.numRows - 1) {
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
                return "";
            } catch (IOException x) {
                return "ERROR: Store Table Failed";
            }
        }

        public String insertInto (String tableName, Row x){
            Table changed = database.get(tableName); //find the table that we need to change
            changed.addRow(x); //go to the addRow method in the table class
            return "";
        }

        public String insertInto (String tableName, ArrayList < TableItem > x){
            Table changed = database.get(tableName);
            changed.addRow(x);
            return "";
        }

        public Table select (String name, ArrayList < String > exprs,
                ArrayList < String > tableNames,
                ArrayList < String > conds){
            Table newTable = database.get(tableNames.get(0));
            for (int i = 1; i < tableNames.size(); i++) {
                newTable = Table.join(name, newTable, database.get(tableNames.get(i)));
            }
            if (exprs.get(0).equals("*")) {
                return newTable;
            }
            return newTable; //TODO CHANGE THIS
//        return newTable.select(name, exprs, newTable, conds);
        }

        public String transact (String query){
            try {
                return Parse.parse(query, this);
            } catch (IOException x) {
                return "ERROR: Transaction error";
            } catch (RuntimeException y) {
                return "ERROR: RunTimeError";
            }
        }

    }
