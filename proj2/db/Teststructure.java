package db;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by vip on 2/21/17.
 */
public class Teststructure {
    public static void main(String[]args) throws IOException{
        Database sql=new Database();
        //test of constructors of tables,rows,cols
        ArrayList names=new ArrayList<String>();
        names.add("school");
        names.add("scores");
        ArrayList types=new ArrayList<String>();
        types.add("string");
        types.add("int");
        sql.createtable("table1",names,types);

        //test of addingrows to table and printing tables
        /*
        ArrayList x=new ArrayList();
        x.add("UC berkely");
        x.add(1000);
        //score correspond to UC Berkley will be 0, which is default NOVALUE of int
        Row berkeley=new Row(x,1);
        sql.insertInto("table1",berkeley);
        ArrayList damn=new ArrayList();
        damn.add("stupid school");
        damn.add(200);
        Row stupid=new Row(damn,2);
        sql.insertInto("table1",stupid);
        System.out.println(sql.print("table1"));

        //create a second table
        ArrayList names2=new ArrayList<String>();
        names2.add("college");
        names2.add("money");
        names2.add("cash");
        ArrayList types2=new ArrayList<String>();
        types2.add("string");
        types2.add("int");
        types2.add("int");
        sql.createtable("table2",names2,types2);

        //add rows to the second table
        ArrayList y=new ArrayList();
        y.add("UCLA");
        y.add(1001);
        y.add(99999);
        Row la=new Row(y,1);
        sql.insertInto("table2",la);
        ArrayList z=new ArrayList();
        z.add("USC");
        z.add(1002);
        z.add(10293838);
        Row sc=new Row(z,2);
        sql.insertInto("table2",sc);
        System.out.println(sql.print("table2"));
        Table joined=Table.join("stanford",sql.getbody().get("table1"),sql.getbody().get("table2"));
        System.out.println(joined.printtable());
        ArrayList rr = new ArrayList();
        rr.add("UCLA");
        rr.add(8888);
        rr.add(5555);
        Row rrr = new Row(rr, 3);
        sql.insertInto("table2", rrr);


        ArrayList names3=new ArrayList<String>();
        names3.add("college");
        names3.add("cash");
        ArrayList types3=new ArrayList<String>();
        types3.add("string");
        types3.add("int");
        sql.createtable("table3",names3,types3);

        ArrayList row1 = new ArrayList();
        row1.add("UCLA");
        row1.add(99999);
        Row row11 = new Row(row1, 1);
        ArrayList row2 = new ArrayList();
        sql.insertInto("table3", row11);
        row2.add("UC Santa Cruz");
        row2.add(9001);
        Row row22 = new Row(row2, 2);
        sql.insertInto("table3", row22);

        System.out.println(sql.print("table3"));

        Table joined2=Table.join("fuckinghard",sql.getbody().get("table2"),sql.getbody().get("table3"));
        System.out.println(joined2.printtable());
        */
        sql.load("fans");
        System.out.println(sql.getbody().get("fans").printtable());
    }
}
