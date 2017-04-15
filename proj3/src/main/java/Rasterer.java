import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.
    QuadTree files;
    String filepath;

    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */
    public Rasterer(String imgRoot) {
        filepath = imgRoot;
        files = new QuadTree();
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>Has dimensions of at least w by h, where w and h are the user viewport width
     * and height.</li>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        Double ullon = params.get("ullon");
        Double ullat = params.get("ullat");
        Double lrlon = params.get("lrlon");
        Double lrlat = params.get("lrlat");
        Double width = params.get("w");
        Double height = params.get("h");
        Node query = new Node(ullon, ullat, lrlon, lrlat, width, height);
        ArrayList<Node> nodelist = new ArrayList<>();
        findquery(files.root, query, nodelist);
        Integer depth1 = nodelist.get(0).imgnum;
        Integer depth2 = depth1.toString().length();
        String[][] result = metamorphsis(nodelist);
        double finalullon = nodelist.get(0).ullong;
        double finalullat = nodelist.get(0).ullat;
        double finallrlon = nodelist.get(nodelist.size() - 1).lrlong;
        double finallrlat = nodelist.get(nodelist.size() - 1).lrlat;
        results.put("render_grid", result);
        results.put("raster_ul_lon", finalullon);
        results.put("raster_ul_lat", finalullat);
        results.put("raster_lr_lon", finallrlon);
        results.put("raster_lr_lat", finallrlat);
        results.put("depth", depth2);
        results.put("query_success", true);
        return results;
    }

    public void findquery(Node root, Node query, ArrayList<Node> x) {
        if (isoverlapping(root, query)) {
            if (root.LonDpp < query.LonDpp || root.imgnum * 10 > 4444444) {
                x.add(root);
            } else {
                findquery(root.NW, query, x);
                findquery(root.NE, query, x);
                findquery(root.SW, query, x);
                findquery(root.SE, query, x);
            }
        }
    }

    public String[][] metamorphsis(ArrayList<Node> x) {
        int rowlength = Rasterer.findrowlength(x);
        int numofrows = x.size() / rowlength;
        String[][] result = new String[numofrows][rowlength];
        PriorityQueue<Node> nodes = new PriorityQueue<>(x.size());
        for (Node node : x) {
            nodes.add(node);
        }
        for (int i = 0; i < numofrows; i++) {
            for (int j = 0; j < rowlength; j++) {
                result[i][j] = filepath + nodes.poll().filename;
            }
        }
        return result;
    }


    public static boolean onthesamerow(Node n1, Node n2) {
        if (n1.ullat == n2.ullat && n1.lrlat == n2.lrlat) {
            return true;
        }
        return false;
    }

    public static int findrowlength(ArrayList<Node> x) {
        Node firstone = x.get(0);
        int counter = 0;
        for (Node element : x) {
            if (onthesamerow(firstone, element)) {
                counter += 1;
            }
        }
        return counter;
    }

    public boolean isoverlapping(Node n1, Node n2) {
        if (n2.lrlong < n1.ullong || n1.lrlong < n2.ullong) {
            return false;
        } else if (n2.lrlat > n1.ullat || n1.lrlat > n2.ullat) {
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        Rasterer wtf = new Rasterer("img/");
        Node query = new Node(-122.3027284165759, 37.88708748276975,
                -122.20908713544797, 37.848731523430196, 305.0, 300.0);
        ArrayList<Node> nodelist = new ArrayList<>();
        wtf.findquery(wtf.files.root, query, nodelist);
        for (Node x : nodelist) {
            System.out.print(x.imgnum + " ");
        }
        System.out.println();
        System.out.println(findrowlength(nodelist));
        String[][] matrix = wtf.metamorphsis(nodelist);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        Node query2 = new Node(-122.24163047377972, 37.87655856892288, -122.24053369025242,
                37.87548268822065, 892.0, 875.0);
        ArrayList<Node> nodelist2 = new ArrayList<>();
        wtf.findquery(wtf.files.root, query2, nodelist2);
        for (Node x : nodelist2) {
            System.out.print(x.imgnum + " ");
        }
        System.out.println();
        System.out.println(findrowlength(nodelist2));
        String[][] matrix2 = wtf.metamorphsis(nodelist2);
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 0; j < matrix2[i].length; j++) {
                System.out.print(matrix2[i][j] + " ");
            }
            System.out.println();
        }
    }
}
