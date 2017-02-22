package db;

/**
 * Created by vip on 2/21/17.
 */
public class NOVALUE {
    Object body;
    //NOVALUE is simply something that we could add to columns as long as we know the column type
    public NOVALUE(String type){
        if (type=="int"){
            body=new Integer(0);
        }
        else if (type=="double"){
            body=new Double(0.0);
        }
        else if (type=="string"){
            body="";
        }
    }

    public Object getbody(){
        return body;
    }
}
