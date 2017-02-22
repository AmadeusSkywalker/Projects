package db;

import java.util.ArrayList;

public class Column<T>/*<T extends Comparable<T>>*/ {
    String name;
    String type;
    ArrayList<T> body;
    //T NaN = null;
    //TableItemComparator<T> compare1;


    public Column(String name1, String type1){
        name=name1;
        type=type1;
        body=new ArrayList<T>();
    }

    public String getname() {
        return name;
    }

    public String getType(){return type;}

    public void addStuff(T x){
        body.add(x);
    }

    public ArrayList<T> getbody() {
        return body;
    }
}


