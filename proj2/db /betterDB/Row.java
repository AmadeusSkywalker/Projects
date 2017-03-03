package betterDB;

import java.util.ArrayList;

/**
 * Created by ErichRathkamp on 3/1/17.
 */
public class Row {
    ArrayList<TableItem> body;

    public Row(ArrayList<TableItem> x) {
        body = x;
    }

    TableItem get(int index) {
        return body.get(index);
    }

}
