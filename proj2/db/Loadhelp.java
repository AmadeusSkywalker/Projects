package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vip on 3/4/17.
 */
public class Loadhelp {
    public static String load(BufferedReader reader, String name, Database x) throws IOException {
        String firstLine = reader.readLine();
        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<String> columnTypes = new ArrayList<>();
        boolean isend = false;
        while (!isend) {
            firstLine = firstLine.trim();
            int firstIndex = firstLine.indexOf(" ");
            if (firstIndex == -1) {
                return "ERROR: No type?";
            }
            String colName = firstLine.substring(0, firstIndex);
            columnNames.add(colName);
            firstLine = firstLine.substring(firstIndex + 1);
            firstLine = firstLine.trim();
            int secondIndex = firstLine.indexOf(",");
            if (secondIndex == 0) {
                return "ERROR: No type?";
            } else if (secondIndex == -1) {
                if (firstLine.contains(" ")) {
                    return "ERROR: Missing commas";
                }
                secondIndex = firstLine.length();
                isend = true;
            }
            String colType = firstLine.substring(0, secondIndex);
            colType=colType.trim();
            if (!colType.equals("string") && !colType.equals("int") && !colType.equals("float")) {
                return "ERROR: Incorrect loaded column type";
            }
            columnTypes.add(colType);
            if (!isend) {
                firstLine = firstLine.substring(secondIndex + 1);
                firstLine = firstLine.trim();
            }
        }
        x.createtable(name, columnNames, columnTypes);
        int rowlength=columnTypes.size();
        String nextline = reader.readLine();
        boolean isend2 = false;
        while (nextline != null) {
            int checkindex = 0;
            int index = 0;
            ArrayList<TableItem> newRow = new ArrayList<>();
            nextline = nextline.trim();
            while (!isend2) {
                if(nextline.equals("")){
                    return "ERROR: Nothing after comma";
                }
                if(checkindex==rowlength){
                    return "ERROR: You have too much";
                }
                int commaindex = nextline.indexOf(",");

                if (commaindex == -1) {
                    /*
                    if (nextline.contains(" ")) {
                        return "ERROR: You miss commas";
                    }
                    */
                //todo:think of cases to deal with tables with no commas
                    if(checkindex+1<rowlength){
                        return "ERROR: You need more";
                    }
                    commaindex = nextline.length();
                    isend2 = true;
                }
                String firstItem = nextline.substring(0, commaindex);
                firstItem=firstItem.trim();
                if (columnTypes.get(index).equals("string")) {
                    if (firstItem.equals("NaN")) {
                        return "ERROR: String cannot be NaN";
                    } else if (firstItem.equals("NOVALUE")) {
                        TableItem newitem = new TableItem(firstItem);
                        newitem.NOVALUE = true;
                        newitem.item = "";
                        newRow.add(newitem);
                    }
                    else {
                        if (firstItem.charAt(0) != '\''
                                || firstItem.charAt(firstItem.length() - 1) != '\'') {
                            return "ERROR: Incorrect String format";
                        } else {
                            firstItem = firstItem.substring(1, firstItem.length() - 1);
                            TableItem newitem = new TableItem(firstItem);
                            newRow.add(newitem);
                        }
                    }
                } else if (columnTypes.get(index).equals("float")) {
                    boolean isnovalue=false;
                    try{
                        float temp=Float.parseFloat(firstItem);
                    }catch(NumberFormatException ex){
                        if (!firstItem.equals("NOVALUE")) {
                            return "ERROR: Should have a float here";
                        } else {
                            TableItem newItem = new TableItem(new Float(0.0));
                            newItem.NOVALUE = true;
                            newItem.item = new Float(0.0);
                            newRow.add(newItem);
                            isnovalue = true;
                        }
                    }
                    if(!isnovalue) {
                        TableItem newItem = new TableItem(Float.valueOf(firstItem));
                        if (firstItem.equals("NaN")) {
                            newItem.NaN = true;
                        }
                        newRow.add(newItem);
                    }
                } else if (columnTypes.get(index).equals("int")) {
                    boolean isnovalue=false;
                    try{
                        int temp=Integer.parseInt(firstItem);
                    }catch(NumberFormatException ex){
                        if (!firstItem.equals("NOVALUE")) {
                            return "ERROR: Should have a int here";
                        } else {
                            TableItem newItem = new TableItem(new Integer(0));
                            newItem.NOVALUE = true;
                            newItem.item = new Integer(0);
                            newRow.add(newItem);
                            isnovalue = true;
                        }
                    }
                    if(!isnovalue) {
                        TableItem newItem = new TableItem(Integer.valueOf(firstItem));
                        if (firstItem.equals("NaN")) {
                            return "ERROR: String cannot be NaN";
                        }
                        newRow.add(newItem);
                    }
                }
                index=index+1;
                checkindex=checkindex+1;
                if(!isend2){
                    nextline=nextline.substring(commaindex+1);
                    nextline=nextline.trim();
                }
            }
            isend2 = false;
            Row realNewRow = new Row(newRow);
            x.insertInto(name, realNewRow);
            nextline = reader.readLine();
        }
        return "";
    }
}
