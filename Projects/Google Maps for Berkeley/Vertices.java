import java.util.HashMap;

/**
 * Created by vip on 4/13/17.
 */
public class Vertices implements Comparable {
    Long id;
    double lon;
    double lat;
    boolean isloc;
    String name;
    boolean issingle;
    double distancefromstart;
    double heuristics;
    Vertices previous;

    public Vertices(Long id1, double lon1, double lat1) {
        id = id1;
        lon = lon1;
        lat = lat1;
        isloc = false;
        issingle = true;
    }

    public int compareTo(Object o) {
        Vertices v = (Vertices) o;
        double comp = (this.distancefromstart + this.heuristics) - (v.distancefromstart + v.heuristics);
        if (comp > 0) {
            return 1;
        } else if (comp < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
