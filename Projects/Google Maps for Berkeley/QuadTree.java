/**
 * Created by vip on 4/10/17.
 */
public class QuadTree{
    Node root;

    public static int reverse(int i){
        int result=0;
        while(i!=0){
            int x=i%10;
            result=result*10+x;
            i=i/10;
        }
        return result;
    }


    public QuadTree(){
         root=new Node(0);
    }

    public static void main(String[]args){
        QuadTree result=new QuadTree();
        Node x111=result.root.NW.NW.NW;
        Node x1=result.root.NW;
        Node x13=x1.SW;
        System.out.println(x1.imgnum);
        System.out.println(x13.LonDpp);
    }
}
