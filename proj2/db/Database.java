package db;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Database {
    private HashMap<String, Table> database;

    public Database() {
        database = new HashMap<String, Table>();
    }

    public void addTable(Table t1) {
        database.put(t1.name, t1);
    }

    public String createtable(String name, ArrayList<String> colnames, ArrayList<String> types) {
        if (database.containsKey(name)) {
            return "ERROR:Table already existed.";
        }
        if (types.size() == 0 || colnames.size() == 0) {
            return "ERROR: Empty tables.";
        }
        if (types.size() != colnames.size()) {
            return "ERROR: Names and types number no match!";
        }
        Table element = new Table(name, colnames, types);
        database.put(name, element); //put the newly created table in the database
        return "";
    }

    public String load(String name) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(name + ".tbl"));
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
                firstLine = firstLine.trim();
            }
        }
        createtable(name, columnNames, columnTypes);

        String nextLine = reader.readLine();
        int rowNum = 1; //Slot 0 taken by header row
        boolean isend2 = false;
        while (nextLine != null) { //runs per line
            int index = 0;
            ArrayList<Object> newRow = new ArrayList<>();
            while (!isend2) { //categorizes items inside each line
                int commaIndex = nextLine.indexOf(",");
                if (commaIndex == -1) {
                    commaIndex = nextLine.length();
                    isend2 = true;
                }
                String firstItem = nextLine.substring(0, commaIndex);
                if (columnTypes.get(index).equals("string")) {
                    firstItem = firstItem.substring(1, firstItem.length() - 1);
                    newRow.add(firstItem);
                } else if (columnTypes.get(index).equals("float")) {
                    newRow.add(Float.valueOf(firstItem));
                } else {
                    newRow.add(Integer.valueOf(firstItem));
                }
                index++;
                if (!isend2) {
                    nextLine = nextLine.substring(commaIndex + 1);
                }
            }
            isend2 = false;
            Row realNewRow = new Row(newRow, rowNum);
            rowNum++;
            insertInto(name, realNewRow);
            nextLine = reader.readLine();
        }
        return "";
    }

    public String store(String name) throws IOException {
        File file = new File(name + ".tbl"); // "./" if filepath doesn't work
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        Table currTable = database.get(name);
        ArrayList<String> headNames = currTable.getcolnames();
        ArrayList<String> headTypes = currTable.getcoltypes();

        for (int i = 0; i < headNames.size(); i++) {
            writer.write(headTypes.get(i) + " ");
            writer.write(headNames.get(i) + ",");
        }
        writer.newLine();
        int numCols = currTable.getNumofcols();
        ArrayList<Row> rowList = currTable.getrows();
        for (int i = 0; i < currTable.getNumofrows() - 1; i++) {
            for (Object item : rowList.get(i).getbody()) {
                if (item instanceof Integer || item instanceof Float) {
                    writer.write(item.toString());
                } else if (item instanceof String) {
                    writer.write((String) item);
                }
                if (i != numCols) {
                    writer.write(",");
                }
            }
            writer.newLine();
        }
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

    public String insertInto(String tablename, Row x) {
        Table changed = database.get(tablename); //find the table that we need to change
        changed.addRow(x); //go to the addRow method in the table class
        return "";
    }

    public String print(String tablename) {
        if (database.containsKey(tablename)) {
            Table changed = database.get(tablename);
            return changed.printtable();
        } else {
            return "ERROR:No such table!.*";
        }
    }

    public Table select(String name, ArrayList<String> exprs,
                        ArrayList<String> tableNames,
                        ArrayList<String> conds) {
        Table newTable = database.get(tableNames.get(0));
        for (int i = 1; i < tableNames.size(); i++) {
            newTable = Table.join(name, newTable, database.get(tableNames.get(i)));
        }
        newTable = Table.select(name, exprs, newTable, conds);
        return newTable;
    }

    public HashMap<String, Table> getbody() {
        return database;
    }

    public String transact(String query) {
        try {
            return Parse.parse(query, this);
        } catch (IOException x) {
            return "Transaction error";
        }
    }
}
