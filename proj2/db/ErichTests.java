package db;

import java.io.IOException;
import java.util.*;

/**
 * Created by ErichRathkamp on 2/28/17.
 */
public class ErichTests {

    public static void main(String[] args) throws IOException {
        Database db = new Database();
        Parse.parse("create table table1 as select * from t1, t2", db);

        TableItemCombiner tic1 = new TableItemCombiner("x+yasa");
        System.out.println(tic1.operation);
        System.out.println(tic1.colOne);
        System.out.println(tic1.colTwo);
        System.out.println(tic1.resultName);

        System.out.println(tic1.combiner(new Float(0.1), new Float(0.2)));

        ArrayList<String> keys1 = new ArrayList<String>();
        ArrayList<String> types1 = new ArrayList<String>();
        keys1.add("x");
        types1.add("int");
        TableItemComparator tcomp1 = new TableItemComparator("x>=6", keys1, types1);
        System.out.println(tcomp1.thing2);

    }
}
