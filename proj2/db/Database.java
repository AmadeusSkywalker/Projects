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

    HashMap<String, Table> getDatabase() {
        return database;
    }

    public Database() {
        database = new HashMap<String, Table>();
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
            return "ERROR: Names and types sizes do not match!";
        }
        Table element = new Table(name, colnames, types);
        //invoke the table constructor to create a new table
        database.put(name, element); //put the newly created table in the database
        return "";
    }

    public String droptable(String name) {
        if (database.containsKey(name)) {
            database.remove(name);
            return "";
        } else {
            return "ERROR: No such table.";
        }
    }

    public String print(String tablename) {
        if (database.containsKey(tablename)) {
            Table changed = database.get(tablename);
            return changed.printtable();
        } else {
            return "ERROR: No such table!";
        }
    }

    public String load(String name, Database x) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(name + ".tbl"));
        String result = Loadhelp.load(reader, name, x);
        return result;
    }

    public String store(String name) {
        try {
            File file = new File(name + ".tbl"); // "./" if filepath doesn't work
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if (!database.containsKey(name)) {
                return "ERROR: Cannot store non-existent table";
            }

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
                    if (tItem.NaN) {
                        writer.write("NaN");
                    } else if (tItem.NOVALUE) {
                        writer.write("NOVALUE");
                    } else if (tItem.type.equals("int") || tItem.type.equals("float")) {
                        if (tItem.type.equals("float")) {
                            writer.write(tItem.item.toString());  //TODO format for float
                        } else {
                            writer.write(tItem.item.toString());
                        }
                    } else if (tItem.type.equals("string")) {
                        writer.write("'" + (String) tItem.item + "'");
                    }
                    if (!(j == rowList.get(i).body.size() - 1)) { //TODO Unsure if it's fixed?
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

    public String insertInto(String tableName, Row x) {
        if (database.containsKey(tableName)) {
            Table changed = database.get(tableName); //find the table that we need to change
            return changed.addRow(x); //go to the addRow method in the table class
        }
        return "ERROR: Table not found";
    }

    public String insertInto(String tableName, ArrayList<TableItem> x) {
        if (database.containsKey(tableName)) {
            Table changed = database.get(tableName); //find the table that we need to change
            return changed.addRow(x); //go to the addRow method in the table class
        }
        return "ERROR: Table not found";
    }

    public Table select(String name, ArrayList<String> exprs,
                        ArrayList<String> tableNames,
                        ArrayList<String> conds) {
        if (!database.containsKey(tableNames.get(0))) {
            throw new RuntimeException("ERROR: Tried to select from nonexistent table");
        }
        Table newTable = database.get(tableNames.get(0));
        for (int i = 1; i < tableNames.size(); i++) {
            newTable = Table.join(name, newTable, database.get(tableNames.get(i)));
        }
        if (exprs.get(0).equals("*")) {
            return newTable;
        }
        return newTable.select(name, exprs, conds);
    }

    public String transact(String query) {
        try {
            return Parse.parse(query, this);
        } catch (IOException x) {
            return "ERROR: Transaction error";
        } catch (RuntimeException y) {
            return y.getMessage();
        }
    }

}
