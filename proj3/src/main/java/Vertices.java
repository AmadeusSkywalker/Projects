import java.util.HashMap;

/**
 * Created by vip on 4/13/17.
 */
public class Vertices {
    Long id;
    double lon;
    double lat;
    boolean isloc;
    HashMap<String,String> extrainfo;
    boolean issingle;

    public Vertices(Long id1,double lon1,double lat1){
        id=id1;
        lon=lon1;
        lat=lat1;
        isloc=false;
        extrainfo=new HashMap<>();
        issingle=true;
    }
}
