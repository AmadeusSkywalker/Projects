import edu.princeton.cs.algs4.Picture;

/**
 * Created by vip on 4/23/17.
 */
public class SeamCarverTest {
    public static void main(String[]args){
        Picture p = new Picture("images/10x12.png");
        SeamCarver sc = new SeamCarver(p);
        double[][]energymat=SCUtility.toEnergyMatrix(sc);
        /*
        for(int i=0;i<energymat.length;i++){
            for(int j=0;j<energymat[i].length;j++){
                System.out.print(energymat[i][j]+" ");
            }
            System.out.println();
        }
        */
        int[]seam=sc.findHorizontalSeam();
    }
}
