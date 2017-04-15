import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest, 
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        LinkedList<Long> result=new LinkedList<>();
        HashMap<Vertices,ArrayList<Vertices>> map=g.ourgraph;
        Vertices start=new Vertices(new Long(0),0.0,0.0);
        Vertices end=new Vertices(new Long(0),0.0,0.0);
        boolean findreallove=false;
        for(Vertices x: map.keySet()){
            if(x.lat==stlat&&x.lon==stlon){
                 start=x;
                 start.distancefromstart=0;
            }
            if(x.lat==destlat&&x.lon==destlat){
                 end=x;
                 end.heuristics=0;
            }
        }
        start.heuristics=g.helpdistance(start,end);
        PriorityQueue<Vertices> fringe=new PriorityQueue<>();
        fringe.add(start);
        while(!fringe.isEmpty()&&!findreallove){

        }

        return new LinkedList<Long>();
    }
}
