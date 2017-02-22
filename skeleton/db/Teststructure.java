package db;
import org.junit.Test;
import java.util.ArrayList;
/**
 * Created by vip on 2/21/17.
 */
public class Teststructure {
    public static void main(String[]args){
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
        ArrayList x=new ArrayList();
        x.add("UC berkely");
        x.add(1000);
        //score correspond to UC Berkley will be 0, which is default NOVALUE of int
        Row berkeley=new Row(x,1);
        sql.insertInto("table1",berkeley);
        System.out.println(sql.print("table1"));

        ArrayList names2=new ArrayList<String>();
        names2.add("college");
        names2.add("money");
        ArrayList types2=new ArrayList<String>();
        types2.add("string");
        types2.add("int");
        sql.createtable("table2",names2,types2);
        ArrayList y=new ArrayList();
        y.add("UCLA");
        y.add(1001);
        Row la=new Row(y,1);
        sql.insertInto("table2",la);
        System.out.println(sql.print("table2"));

        //TODO:finish cartesian join so that there is no error
        Table joined=Table.join("stanford",sql.getbody().get("table1"),sql.getbody().get("table2"));
        System.out.println(sql.print("stanford"));
        //test of droppint table
        sql.droptable("table1");
        System.out.println(sql.print("table1"));
    }
}
