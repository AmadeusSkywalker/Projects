package db;

import db.Table;

import java.io.IOException;

/**
 * Created by ErichRathkamp on 3/2/17.
 */
public class LoadTests {
    public static void main(String[] args) throws IOException{
        Database sql = new Database();
        sql.load("fans",sql);
        System.out.println(sql.print("fans"));
        sql.store("fans");

        sql.load("joint1",sql);
        System.out.println(sql.print("joint1"));

        sql.load("joint2",sql);
        System.out.println(sql.print("joint2"));
        Table t1 = sql.getDatabase().get("joint1");
        Table t2 = sql.getDatabase().get("joint2");
        Table t3 = sql.getDatabase().get("fans");
        Table joined = Table.join("dummy", t1, t2);
        System.out.println(joined.printtable());
        Table joined2 = Table.join("dummy", t3, t1);
        System.out.println(joined2.printtable());

    }
}
