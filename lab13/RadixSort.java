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
        String[] cloned=new String[asciis.length];
        System.arraycopy(asciis,0,cloned,0,asciis.length);
        String[] sorted = new String[asciis.length];
        int longeststring = 0;
        for (String x : asciis) {
            if (x.length() > longeststring) {
                longeststring = x.length();
            }
        }
        for(int i=0;i<cloned.length;i++){
            if(cloned[i].length()<longeststring){
                for (int j = 0; j < longeststring - cloned[i].length(); j++) {
                    char c = Character.MIN_VALUE;
                    cloned[i] += c;
                }
            }
        }
        for (int i = 1; i < longeststring + 1; i++) {
            int[] counts = new int[257];
            int[] start = new int[counts.length];
            for (String x : cloned) {
                char c = x.charAt(x.length() - i);
                counts[(int) c + 1] += 1;
            }
            makestart(start, counts);
            for (String x : cloned) {
                int charn = (int) x.charAt(x.length() - i) + 1;
                int startindex = start[charn];
                sorted[startindex] = x;
                start[charn] += 1;
            }
            System.arraycopy(sorted,0,cloned,0,sorted.length);
        }
        for(int i=0;i<sorted.length;i++){
            while(sorted[i].indexOf(Character.MIN_VALUE)!=-1){
                sorted[i]=sorted[i].substring(0,sorted[i].length()-1);
            }
        }
        return sorted;
    }


    public static void makestart(int[] start, int[] counts) {
        int sum = 0;
        for (int j = 0; j < counts.length; j++) {
            start[j] = sum;
            sum += counts[j];
        }
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
        String[] stupid = new String[2];
        char a = (char) 254;
        System.out.println(a);
        stupid[0] = "cdsxa";
        stupid[1] = "cdge";
        stupid = sort(stupid);
        for (String x : stupid) {
            System.out.println(x);
        }
    }
}
