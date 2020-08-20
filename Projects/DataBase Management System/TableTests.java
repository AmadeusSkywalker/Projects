package db;

import java.util.ArrayList;
/**
 * Created by ErichRathkamp on 3/1/17.
 */
public class TableTests {

    public static void main(String[] args) {
        String s1 = "cheese";
        String s2 = "cheese";
        if (s1.equals(s2)) {
            System.out.println("cheese is cheese" + "\n");
        }

        Database sql = new Database();
        //test of constructors of tables,rows,cols
        ArrayList<String> t1names = new ArrayList<>();
        t1names.add("School");
        t1names.add("Scores");
        ArrayList<String> types = new ArrayList<>();
        types.add("string");
        types.add("int");
        sql.createtable("table1", t1names, types);
        ArrayList<TableItem> row1 = new ArrayList<>();
        row1.add(new TableItem("UCLA"));
        row1.add(new TableItem(900));
        sql.insertInto("table1", row1);
        System.out.println(sql.print("table1"));

        ArrayList<String> t2names = new ArrayList<>();
        t2names.add("School");
        t2names.add("Tuition");
        //t2names.add("Jamie");
        ArrayList<String> t2types = new ArrayList<>();
        t2types.add("string");
        t2types.add("int");
        //t2types.add("string");
        sql.createtable("table2", t2names, t2types);
        ArrayList<TableItem> t2row1 = new ArrayList<>();
        t2row1.add(new TableItem("UCLA"));
        t2row1.add(new TableItem(60000));
        //t2row1.add(new TableItem("james"));
        sql.insertInto("table2", t2row1);
        Table T1 = sql.getDatabase().get("table1");
        Table T2 = sql.getDatabase().get("table2");
        System.out.println(sql.print("table1"));
        System.out.println(sql.print("table2"));
        System.out.println(Table.join("dummy", T1, T2).printtable());

        sql.store("table1");




    }
}