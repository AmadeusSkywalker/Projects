package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Database {
    public HashMap<String, Table> database;

    public Database() {
        database = new HashMap<String, Table>();
    }

    public String createtable(String name, ArrayList<String> colnames, ArrayList<String> types) {
        if (database.containsKey(name)) {
            throw new RuntimeException("Table already existed");
        }
        if (types.size() == 0 || colnames.size() == 0) {
            throw new RuntimeException("Empty table");
        }
        if (types.size() != colnames.size()) {
            throw new RuntimeException("Names and types number no match!");
        }
        Table element = new Table(name, colnames, types); //invoke the table constructor to create a new table
        database.put(name, element); //put the newly created table in the database
        return "";
    }

    public String load(String name) {

        return "YOUR CODE HERE";
    }

    public String store(String name) {
        return "YOUR CODE HERE";
    }

    public String droptable(String name) {
        if (database.containsKey(name)){
            database.remove(name);
            return"";
        }
        else{
            throw new RuntimeException("Hey!No such table!");
        }
    }

    public String insertInto(String tablename, Row x) {
        Table changed=database.get(tablename); //find the table that we need to change
        changed.addRow(x); //go to the addRow method in the table class
        return "";
    }

    public String print(String tablename) {
        if (database.containsKey(tablename)) {
            Table changed = database.get(tablename);
            return changed.printtable();
        }
        else{
            throw new RuntimeException("No such table!");
        }
    }

    public String select() {

        return "YOUR CODE HERE";
    }

    public HashMap<String,Table> getbody(){
        return database;
    }

    public String transact(String query) {
        return "YOUR CODE HERE";
    }
}
