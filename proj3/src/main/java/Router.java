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
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
                                                double stlat, double destlon, double destlat) {
        LinkedList<Long> result = new LinkedList<>();
        HashMap<Vertices, ArrayList<Vertices>> map = g.ourgraph;
        HashMap<Long, Vertices> index = g.allnodes;
        boolean findreallove = false;
        Long end1 = g.closest(destlon, destlat);
        Vertices end = index.get(end1);
        Long start1 = g.closest(stlon, stlat);
        Vertices start = index.get(start1);
        end.heuristics = 0;
        start.distancefromstart = 0;
        start.previous = null;
        start.heuristics = g.helpdistance(start, end);
        PriorityQueue<Vertices> fringe = new PriorityQueue<>();
        fringe.add(start);
        while (!fringe.isEmpty() && !findreallove) {
            Vertices current = fringe.poll();
            if (current.heuristics != 0) {
                for (Long close : g.adjacent(current.id)) {
                    Vertices neighbor = index.get(close);
                    if (current.previous == null) {
                        neighbor.previous = current;
                        neighbor.heuristics = g.helpdistance(neighbor, end);
                        neighbor.distancefromstart = current.distancefromstart
                                + g.helpdistance(neighbor, current);
                        fringe.add(neighbor);
                    } else if (neighbor.id != current.previous.id) {
                        if (neighbor.previous != null) {
                            Vertices exwife = neighbor.previous;
                            double x1 = exwife.distancefromstart + g.helpdistance(exwife, neighbor);
                            double x2 = current.distancefromstart +
                                    g.helpdistance(current, neighbor);
                            if (x1 > x2) {
                                neighbor.previous = current;
                                neighbor.heuristics = g.helpdistance(neighbor, end);
                                neighbor.distancefromstart = current.distancefromstart
                                        + g.helpdistance(neighbor, current);
                                fringe.add(neighbor);
                            }
                        } else {
                            neighbor.previous = current;
                            neighbor.heuristics = g.helpdistance(neighbor, end);
                            neighbor.distancefromstart = current.distancefromstart
                                    + g.helpdistance(neighbor, current);
                            fringe.add(neighbor);
                        }
                    }
                }
            } else {
                findreallove = true;
                LinkedList<Long> temporary = new LinkedList<>();
                while (current != null) {
                    temporary.add(current.id);
                    current = current.previous;
                }
                while (!temporary.isEmpty()) {
                    result.add(temporary.removeLast());
                }
            }
        }
        for (Long index1 : index.keySet()) {
            Vertices newv = index.get(index1);
            newv.previous = null;
        }
        return result;
    }


    public static void main(String[] args) {
        GraphDB g = new GraphDB("berkeley.osm");
        LinkedList<Long> result = Router.shortestPath(g, -122.25645867831877, 37.85010232750908,
                -122.25897184667542, 37.849578805572065);
        System.out.println(result);
        LinkedList<Long> result2 = Router.shortestPath(g, -122.25645867831877, 37.85010232750908,
                -122.25897184667542, 37.849578805572065);
        System.out.println(result2);
    }
}


