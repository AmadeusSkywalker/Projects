package db;

/**
 * Created by vip on 2/21/17.
 */

import java.util.ArrayList;

//There is no row of the column headers, it just doesn't exist
public class Row<Object> {
      ArrayList<Object> body;
      int index;

      public Row(ArrayList<Object> x,int rownum){
          body=x;
          index=rownum;
      }

      public ArrayList<Object> getbody(){
          return body;
      }
}
