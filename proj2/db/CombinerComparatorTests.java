package db;

import db.TableItem;
import java.util.ArrayList;
/**
 * Created by ErichRathkamp on 3/2/17.
 */
public class CombinerComparatorTests {
    public static void main (String[] args) {
        /*
        TableItemCombiner TIC = new TableItemCombiner("x+y[as]col1");
        System.out.println(TIC.resultName);
        System.out.println(TIC.colOne);
        System.out.println(TIC.colTwo);
        System.out.println(TIC.operation);
        */

        TableItem nullItem = new TableItem(null);
        System.out.println(nullItem.NaN);

        Object obj1 = Float.valueOf("1");
        TableItem TI1 = new TableItem(obj1);
        System.out.println(TI1.type);

        TableItem item1 = new TableItem(5);
        System.out.println(item1.type);

        TableItem TI2 = new TableItem((Float) TI1.item + (Integer) item1.item);
        System.out.println(TI2.type);
        System.out.println(TI2.item);


        ArrayList<String> names = new ArrayList<>();
        names.add("x");
        names.add("y");
        ArrayList<String> types = new ArrayList<>();
        types.add("float");
        types.add("float");
        ArrayList<TableItem> rowContents = new ArrayList<>();
        rowContents.add(new TableItem(Float.valueOf(5)));
        rowContents.add(new TableItem(Float.valueOf(6)));
        Row newRow = new Row(rowContents);

        TableItemCombiner TIC = new TableItemCombiner("x+y[as]col1", names, types);
        TableItem combined = TIC.combiner(newRow);
        System.out.println(combined.item);

        //TableItem three = TIC.combiner(new TableItem(new Float(0.0)), new TableItem(new Float(0.4)));
        //System.out.println(three.item);

    }
}
