package db;

import java.io.IOException;

/**
 * Created by ErichRathkamp on 3/2/17.
 */
public class LoadTests {
    public static void main(String[] args) throws IOException{
        Float f1 = Float.valueOf("0.4");
        if (f1 instanceof Float) {
            System.out.println("True");
        }
        System.out.println(Float.valueOf("0.5"));

        Database sql = new Database();
        sql.load("fans", sql);
        System.out.println(sql.print("fans"));
        sql.store("fans");

        sql.load("joint1", sql);
        System.out.println(sql.print("joint1"));

        sql.load("joint2", sql);
        System.out.println(sql.print("joint2"));
        Table t1 = sql.getDatabase().get("joint1");
        Table t2 = sql.getDatabase().get("joint2");
        Table t3 = sql.getDatabase().get("fans");
        Table joined = Table.join("dummy", t1, t3);
        System.out.println(joined.printtable());
        Table joined2 = Table.join("dummy", joined, t1);
        System.out.println(joined2.printtable());

        TableItem float1 = new TableItem(new Float(0.5));
        TableItem float2 = new TableItem(new Float(4));
        System.out.println((Float) float1.item - (Float) float2.item);

        if (!((Float) float1.item + Float.valueOf("3.5") > (Float) float2.item)
                && !((Float) float1.item + Float.valueOf("3.5") <
                (Float) float2.item)) {
            System.out.println("4 = 4");
        }

        Parse.parse("create table table1 as select * from joint1, joint2", sql);
        System.out.println(sql.print("table1"));

    }
}
