/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 **/
public class RadixSort {

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis) {
        String[] cloned=asciis.clone();
        String[] sorted = new String[asciis.length];
        int longeststring = 0;
        for (String x : asciis) {
            if (x.length() > longeststring) {
                longeststring = x.length();
            }
        }


        for(int i=1;i<longeststring+1;i++){
            int[] counts=new int[257];
            int[] start=new int[counts.length];
            for(String x:cloned){
               if(x.length()-i<0){
                   counts[0]+=1;
               }else{
                   char c=x.charAt(x.length()-i);
                   counts[(int)c+1]+=1;
               }
            }
            for(int f=0;f<counts.length;f++){
                System.out.print(counts[f]+" ");
            }
            System.out.println();
            int sum=0;
            for(int j=0;j<counts.length;j++){
                start[j]=sum;
                sum+=counts[j];
            }
            for(int f=0;f<start.length;f++){
                System.out.print(start[f]+" ");
            }
            System.out.println();
            for(String x:cloned){
                int charn=0;
                if(x.length()-i>=0){
                    charn=(int)x.charAt(x.length()-i)+1;
                }
                int startindex=start[charn];
                sorted[startindex]=x;
                start[charn]+=1;
            }
            for(int f=0;f<sorted.length;f++){
                System.out.print(sorted[f]+" ");
            }
            System.out.println();
            cloned=sorted;
        }
        return sorted;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     * destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start  int for where to start sorting in this method (includes String at start)
     * @param end    int for where to end sorting in this method (does not include String at end)
     * @param index  the index of the character the method is currently sorting on
     **/
    private static void sortHelper(String[] asciis, int start, int end, int index) {
        //TODO use if you want to
    }

    public static void main(String[] args) {
        String[] stupid=new String[3];
        stupid[0]="abcd";
        stupid[1]="abc";
        stupid[2]="abd";
        stupid=sort(stupid);
        for(String x:stupid) {
            System.out.println(x);
        }
    }
}
