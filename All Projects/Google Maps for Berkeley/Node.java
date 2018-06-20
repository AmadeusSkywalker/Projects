/**
 * Created by vip on 4/12/17.
 */
public class Node implements Comparable{
    double ullat,ullong,lrlat,lrlong;
    Node NW, NE, SE, SW;   // four subtrees
    int imgnum;
    String filename;
    Double LonDpp;

    Node(int index){
        imgnum=index;
        ullat=MapServer.ROOT_ULLAT;
        ullong=MapServer.ROOT_ULLON;
        lrlat=MapServer.ROOT_LRLAT;
        lrlong=MapServer.ROOT_LRLON;
        if(index==0){
            filename="root.png";
        }else{
            filename=index+".png";
            int reversename=QuadTree.reverse(imgnum);
            while(reversename!=0){
                int x=reversename%10;
                if(x==1){
                    lrlat=(ullat+lrlat)/2;
                    lrlong=(ullong+lrlong)/2;
                }
                else if(x==2){
                    ullong=(ullong+lrlong)/2;
                    lrlat=(ullat+lrlat)/2;
                }
                else if(x==3){
                    ullat=(ullat+lrlat)/2;
                    lrlong=(ullong+lrlong)/2;
                }
                else if(x==4){
                    ullong=(ullong+lrlong)/2;
                    ullat=(ullat+lrlat)/2;
                }
                reversename=reversename/10;
            }
        }
        LonDpp=(lrlong-ullong)/MapServer.TILE_SIZE;
        if(imgnum<=444444) {
            NW = new Node(imgnum * 10 + 1);
            NE = new Node(imgnum * 10 + 2);
            SW = new Node(imgnum * 10 + 3);
            SE = new Node(imgnum * 10 + 4);
        }
    }

    Node(double ullong1,double ullat1,double lrlong1,double lrlat1,double width,double height){
        this.ullong=ullong1;
        this.ullat=ullat1;
        this.lrlong=lrlong1;
        this.lrlat=lrlat1;
        LonDpp=(lrlong-ullong)/width;
    }

    public int compareTo(Object o){
        Node x=(Node) o;
        if(this.ullat!=x.ullat) {
            double com = x.ullat - this.ullat;
            if (com > 0.0) {
                return 1;
            } else if (com < 0.0) {
                return -1;
            } else {
                return 0;
            }
        }
        else{
            double com2=this.ullong-x.ullong;
            if (com2 > 0.0) {
                return 1;
            } else if (com2 < 0.0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
