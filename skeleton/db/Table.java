package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

/**
 * Created by vip on 2/19/17.
 */
public class Table {
    HashMap<String, Column> columns;
    ArrayList<String> colnames;
    ArrayList<String> coltypes;
    ArrayList<Row> rows;
    String name;
    int numofrows;
    int numofcols;

    public Table(String tablename, ArrayList<String> names, ArrayList<String> types) {
        //The table constructor creates a first row and add this row to the rows variable
        name = tablename;
        colnames = names;
        columns = new HashMap<String, Column>();
        ArrayList<String> first = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            columns.put(names.get(i), new Column(names.get(i), types.get(i)));
            first.add(names.get(i) + " " + types.get(i));
        }
        rows = new ArrayList<Row>();
        Row firstrow = new Row(first, 0);
        rows.add(firstrow);
        numofcols = names.size();
        numofrows = rows.size();
    }

    public void addRow(Row x) {
        ArrayList rowbody = x.getbody();//get the body of the row we want to add, which is an arraylist
        ArrayList newrow = new ArrayList();
        int index = 0;  //this index is used to track the numofcols we have iterated through
        for (Column a : columns.values()) {
            //when index is still less than the length of the newly added row, we just add elements of row to each column
            // Also, we add elements to the new row
            if (index < rowbody.size()) {
                Object m = rowbody.get(index);
                a.addStuff(m);
                newrow.add(m);
                index = index + 1;
            }
            //after index is bigger than the size of rowbody but still less than numofcols, we add NOVALUE to the rest of cols
            //Also,we add elements to the new row
            else if (index < numofcols) {
                Object h = new NOVALUE(a.getType()).getbody();
                a.addStuff(h);
                newrow.add(h);
            }
        }
        Row realrow = new Row(newrow, numofrows);
        numofrows = numofrows + 1;
        rows.add(realrow);
    }

    public String printtable() {
        String result = "";
        for (Row row : rows) {
            //printing the table row by row
            String currentrow = "";
            ArrayList tobeprinted = row.getbody();
            for (Object h : tobeprinted) {
                //in each row, add the string representation of the object to the result
                currentrow = currentrow + h.toString() + "     ";
            }
            result = result + currentrow + '\n';
        }
        return result;
    }

    public ArrayList<String> getcolnames() {
        return colnames;
    }

    public ArrayList<String> getcoltypes() {
        return coltypes;
    }

    public ArrayList<Row> getrows() {
        return rows;
    }

    public int getNumofrows(){
        return numofrows;
    }


    public static Table join(String name, Table t1, Table t2) {
        ArrayList<String> t1names = t1.getcolnames();
        ArrayList<String> t2names = t2.getcolnames();
        ArrayList<String> t1types = t1.getcoltypes();
        ArrayList<String> t2types = t2.getcoltypes();
        ArrayList<String> samekeys = new ArrayList<>();
        ArrayList<String> allkeys = new ArrayList<>();
        ArrayList<String> alltypes = new ArrayList<>();
        //find the same keys that both tables share
        for (int i = 0; i < t1names.size(); i++) {
            for (int k = 0; k < t2names.size(); k++) {
                if (t1names.get(i) == t2names.get(k)) {
                    samekeys.add(t1names.get(i));
                }
            }
        }
        //put all the keys and types in order in a new arraylist,later used for cartesian join
        for (int i = 0; i < t1names.size(); i++) {
            allkeys.add(t1names.get(i));
            alltypes.add(t1types.get(i));
        }
        for (int i = 0; i < t2names.size(); i++) {
            allkeys.add(t2names.get(i));
            alltypes.add(t2types.get(i));
        }
        //check whether two tables have same keys, if yes, innerJoin; if not, cartesianJoin
        if (samekeys.size() == 0) {
            return cartesianJoin(name, t1, t2, allkeys, alltypes);
        } else {
            return innerJoin(name, t1, t2, samekeys);
        }
    }


    private static Table cartesianJoin(String name, Table t1, Table t2, ArrayList<String> names, ArrayList<String> types) {
        Table joined = new Table(name, names, types);
        int index=1;
        //for each new row, just add the previous rows together, and add the new row to the new table
        for (int i=0;i<t1.getNumofrows();i++){
            for (int k=0;k<t2.getNumofrows();k++){
                Row x=t1.getrows().get(i);
                Row y=t2.getrows().get(k);
                ArrayList bodyx=x.getbody();
                ArrayList bodyy=y.getbody();
                ArrayList bigbody=new ArrayList();
                bigbody.addAll(bodyx);
                bigbody.addAll(bodyy);
                Row xy=new Row(bigbody,index);
                index=index+1;
                joined.addRow(xy);
            }
        }
        return joined;
    }

    private static Table innerJoin(String name, Table t1, Table t2, ArrayList<String> samekeys) {
           return t1;
    }


}
