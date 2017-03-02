package db;

import java.lang.reflect.Array;
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
    int numofrows; //Accounts for the row of column headers, even though it's not in the row list
    int numofcols;

    public Row getRow(int x) {
        return rows.get(x + 1);
    }

    public Table(String tablename, ArrayList<String> names, ArrayList<String> types) {
        //The table constructor creates a first row and add this row to the rows variable
        name = tablename;
        colnames = names;
        coltypes=types;
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
            //when index is still less than the length of the newly added row, we just add elements of row to each
            // column
            // Also, we add elements to the new row
            if (index < rowbody.size()) {
                Object m = rowbody.get(index);
                a.addStuff((Comparable) m);
                newrow.add(m);
                index = index + 1;
            }
            //after index is bigger than the size of rowbody but still less than numofcols, we add NOVALUE to
            // the rest of cols
            //Also,we add elements to the new row
            else if (index < numofcols) {
                Object h = new NOVALUE(a.getType()).getbody();
                a.addStuff((Comparable) h);
                newrow.add(h);
            }
        }
        Row realrow = new Row(newrow, numofrows);
        numofrows = numofrows + 1;
        rows.add(realrow);
    }

    public static Table select(String name, ArrayList<String> exprs, Table t1, ArrayList<String> conds) {
        //TODO check if it's an empty table
        ArrayList<String> resultNames = new ArrayList<>();
        ArrayList<String> resultTypes = new ArrayList<>();

        ArrayList<Integer> legalRows = new ArrayList<>();
        for (int i = 0; i < t1.getNumofrows(); i++) {
            legalRows.add(i);
        }
        ArrayList<TableItemComparator> comparators = new ArrayList<>();
        for (String cond : conds) {
            comparators.add(new TableItemComparator(cond, t1.colnames, t1.coltypes));
        }
        for (TableItemComparator comparator : comparators) {
            for (int row : legalRows) {
                if (!(comparator.compare(t1.getRow(row).body))) { //TODO check stupid row number
                    legalRows.remove(row);
                }
            }
        }

        Table filteredTable = new Table(name, t1.colnames, t1.coltypes); //TODO check if empty
        for (int row : legalRows) {
            filteredTable.addRow(t1.getRow(row));
        }

        if (exprs.get(0).equals("*")) {
            return filteredTable; //TODO make it return only valid rows
        }
            ArrayList<TableItemCombiner> newColCombiners = new ArrayList<>();
            ArrayList<ArrayList<Object>> newCols = new ArrayList<>();
            for (String expr : exprs) {
                newColCombiners.add(new TableItemCombiner(expr));
            }




            for (TableItemCombiner ItemCombiner : newColCombiners) { //For every expression
                ArrayList<Object> newCol = new ArrayList<>();
                resultNames.add(ItemCombiner.resultName); //Adds the name of the new column

                //Combines the two columns and adds them to the list of new columns
                if (ItemCombiner.colTwo == null) {
                    ArrayList<Object> colOne = filteredTable.columns.get(ItemCombiner.colOne).body;
                    newCols.add(colOne);
                } else {
                    ArrayList<Object> colOne = filteredTable.columns.get(ItemCombiner.colOne).body; //same length
                    ArrayList<Object> colTwo = filteredTable.columns.get(ItemCombiner.colTwo).body;
                    for (int i = 0; i < colOne.size(); i++) {
                        newCol.add(ItemCombiner.combiner(colOne.get(i), colTwo.get(i)));

                    }
                    newCols.add(newCol);
                }

                if (newCol.get(0) instanceof String) {
                    resultTypes.add("string");
                } else if (newCol.get(0) instanceof Integer) {
                    resultTypes.add("int");
                } else if (newCol.get(0) instanceof Float) {
                    resultTypes.add("float");
                }

            }
        Table returnedTable = new Table(name, resultNames, resultTypes);
        for (int i = 0; i < newCols.get(0).size(); i++) {
            ArrayList<Object> newRow = new ArrayList<>();
            for (ArrayList<Object> col : newCols) {
                newRow.add(col.get(i));
            }
            Row trueNewRow = new Row(newRow, i);
            returnedTable.addRow(trueNewRow);
        }
        return returnedTable;
    }


    public String printtable() {
        String result = "";
        boolean isfirstrow=true;
        for (Row row : rows) {
            //printing the table row by row
            String currentrow = "";
            ArrayList tobeprinted = row.getbody();
            int index = 0;
            for (Object h : tobeprinted) {
                //in each row, add the string representation of the object to the result
                if (!isfirstrow) {
                    if (h instanceof String) {
                        currentrow = currentrow + "'" + h.toString() + "'" + ",";
                    } else {
                        currentrow = currentrow + h.toString() + ",";
                    }
                    index = index + 1;
                    if (index == tobeprinted.size()) {
                        currentrow = currentrow.substring(0, currentrow.length() - 1);
                    }
                }
                else{
                    currentrow=currentrow+h.toString()+",";
                    index+=1;
                    if(index==tobeprinted.size()){
                        currentrow=currentrow.substring(0,currentrow.length()-1);
                    }
                }
            }
            isfirstrow=false;
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
        return numofrows - 1;
    }

    int getNumofcols(){
        return numofcols;
    }

    public HashMap<String, Column> getColumns(){
        return columns;
    }


    public static Table join(String name, Table t1, Table t2) {
        ArrayList<String> t1names = t1.getcolnames();
        ArrayList<String> t2names = t2.getcolnames();
        ArrayList<String> t1types = t1.getcoltypes();
        ArrayList<String> t2types = t2.getcoltypes();
        ArrayList<String> samekeys = new ArrayList<>();
        ArrayList<String> sameTypes = new ArrayList<>();
        ArrayList<String> allkeys = new ArrayList<>();
        ArrayList<String> alltypes = new ArrayList<>();
        //find the same keys that both tables share
        for (int i = 0; i < t1names.size(); i++) {
            for (int k = 0; k < t2names.size(); k++) {
                if (t1names.get(i) == t2names.get(k)) {
                    samekeys.add(t1names.get(i));
                    sameTypes.add(t1types.get(i));
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
            return innerJoin(name, t1, t2, samekeys, sameTypes);
        }
    }


    private static Table cartesianJoin(String name, Table t1, Table t2, ArrayList<String> names,
                                       ArrayList<String> types) {
        Table joined = new Table(name, names, types);
        int index=1;
        //for each new row, just add the previous rows together, and add the new row to the new table
        for (int i=1;i<t1.getNumofrows();i++){
            for (int k=1;k<t2.getNumofrows();k++){
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

    private static Table innerJoin(String name, Table t1, Table t2, ArrayList<String> samekeys,
                                   ArrayList<String> sametypes) {
        //Below line makes a new table with the correct arrangement of headers
        Table result=Table.innerjoinhelper(name,t1,t2,samekeys,sametypes);
        int newRowNums = 0;
        for (int i = 1; i < t1.numofrows; i++) {
            for (int j = 1; j < t2.numofrows; j++) {
                int matchedItems = 0;
                ArrayList<Object> newRow = new ArrayList<>();
                for (String key : samekeys) { //Need to know what column we're comparing items in
                    //Below line compares elements in columns in a gross way
                    if (t1.columns.get(key).body.get(i - 1).equals(t2.columns.get(key).body.get(j - 1))) {
                        // == comparison only works with integers up some number in the hundreds
                        matchedItems += 1;
                        newRow.add(t1.columns.get(key).body.get(i - 1));
                    }
                }
                if (matchedItems == samekeys.size()) {
                    for (String colname : t1.colnames) {
                        if (!samekeys.contains(colname)) {
                            newRow.add(t1.columns.get(colname).body.get(i - 1));
                        }
                    }
                    for (String colname : t2.colnames) {
                        if (!samekeys.contains(colname)) {
                            newRow.add(t2.columns.get(colname).body.get(j - 1));
                        }
                    }
                    result.addRow(new Row(newRow, newRowNums));
                    newRowNums += 1;
                }

            }

        }
        return result;
    }










    private static Table innerjoinhelper(String name, Table t1, Table t2, ArrayList<String> samekeys,
                             ArrayList<String> sametypes){
        ArrayList<String> unsharedNames = new ArrayList<>();
        ArrayList<String> unsharedTypes = new ArrayList<>();
        for (String colName : t1.colnames) { //Adds all non-shared columns from left array to unsharedNames
            if (!samekeys.contains(colName)) {
                unsharedNames.add(colName);
                unsharedTypes.add(t1.columns.get(colName).getType());
            }
        }
        for (String colName : t2.colnames) { //Adds all non-shared columns from right array to unsharedNames
            if (!samekeys.contains(colName)) {
                unsharedNames.add(colName);
                unsharedTypes.add(t2.columns.get(colName).getType());
            }
        }
        ArrayList<String> totalNames = new ArrayList<>();
        ArrayList<String> totalTypes = new ArrayList<>();
        totalNames.addAll(samekeys);
        totalNames.addAll(unsharedNames);
        totalTypes.addAll(sametypes);
        totalTypes.addAll(unsharedTypes);
        Table joined = new Table(name, totalNames, totalTypes);
        return joined;
    }

}
