package betterDB;

/**
 * Created by ErichRathkamp on 3/2/17.
 */
public class CombinerComparatorTests {
    public static void main (String[] args) {
        TableItemCombiner TIC = new TableItemCombiner("x+y[as]col1");
        System.out.println(TIC.resultName);
        System.out.println(TIC.colOne);
        System.out.println(TIC.colTwo);
        System.out.println(TIC.operation);

        TableItem three = TIC.combiner(new TableItem(1), new TableItem(2));
        System.out.println(three.item);

    }
}
