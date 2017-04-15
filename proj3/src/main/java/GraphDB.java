import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    public static HashMap<Vertices,ArrayList<Vertices>> ourgraph;
    public static HashMap<Long,Vertices> allnodes;

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        ourgraph=new HashMap<>();
        allnodes=new HashMap<>();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // TODO: Your code here.
        Iterator<Vertices> iter=ourgraph.keySet().iterator();
        while(iter.hasNext()){
            Vertices current=iter.next();
            if(current.issingle==true){
                iter.remove();
            }
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> result=new ArrayList<>();
        for(Long id:allnodes.keySet()){
            result.add(id);
        }
        return result;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        ArrayList<Long> result=new ArrayList<>();
        Vertices markedpoint=allnodes.get(v);
        ArrayList<Vertices> allverts=ourgraph.get(markedpoint);
        for(Vertices i:allverts){
            result.add(i.id);
        }
        return result;
    }

    double distance(long v, long w){
        Vertices n1=allnodes.get(v);
        Vertices n2=allnodes.get(w);
        double x=Math.abs(n1.lon-n2.lon);
        double y=Math.abs(n1.lat-n2.lat);
        return Math.sqrt(x*x+y*y);
    }

    double helpdistance(Vertices n1,Vertices n2){
        double x=Math.abs(n1.lon-n2.lon);
        double y=Math.abs(n1.lat-n2.lat);
        return Math.sqrt(x*x+y*y);
    }


    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        Long love=new Long(19981013);
        long smallest=Long.MAX_VALUE;
        Vertices smallvertex=new Vertices(love,lon,lat);//just initialize it
        Vertices target=new Vertices(love,lon,lat);
        for(Vertices x:ourgraph.keySet()){
           long distance=new Double(helpdistance(x,target)).longValue();
           if(distance<smallest){
               smallest=distance;
               smallvertex=x;
           }
        }
        return smallvertex.id;
    }
    //TODO: iterate over all the points,find smallest distance

    /** Longitude of vertex v. */
    double lon(long v) {
        Vertices marked=allnodes.get(v);
        return marked.lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        Vertices marked=allnodes.get(v);
        return marked.lat;
    }
}