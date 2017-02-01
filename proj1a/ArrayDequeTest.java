public class ArrayDequeTest{

    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }

    public static void generalTest(){
        boolean passed=true;
        System.out.println("Running add tests.\n");
        ArrayDeque<String> army=new ArrayDeque<>();
        passed=army.isEmpty()&&passed;
        army.addFirst("soldiers");
        passed=checkSize(1,army.size())&&passed;
        army.addLast("blasters");
        army.addLast("generals");
        army.addLast("guns");
        army.addLast("tanks");
        army.removeFirst();
        passed=checkSize(4,army.size())&&passed;
        army.removeLast();
        String weapon=army.get(2);
        passed=(weapon=="guns")&&passed;
        army.printDeque();
        printTestStatus(passed);
    }

    public static void resizeTest(){
        boolean passed=true;
        ArrayDeque<String> army=new ArrayDeque<>();
        passed=army.isEmpty()&&passed;
        army.addFirst("soldiers");
        passed=checkSize(1,army.size())&&passed;
        army.addLast("blasters");
        army.addLast("generals");
        army.addLast("guns");
        army.addLast("tanks");
        army.addLast("x-wings");
        army.addLast("battleships");
        army.addLast("fighters");
        army.addLast("warriors");
    }

    public static void gradescopeTest(){
        ArrayDeque<Integer> nums=new ArrayDeque<>();
        nums.addFirst(0);
        int a=nums.removeLast();    // ==> 0
        nums.addLast(2);
        int b=nums.removeFirst();    // ==> 2
        nums.addLast(4);
        nums.addFirst(5);
        nums.addLast(6);
        nums.addLast(7);
        nums.addFirst(8);
        nums.addFirst(9);
        nums.addLast(10);
        nums.addFirst(11);
        nums.addFirst(12);
        nums.addLast(13);
        int c=nums.removeFirst();    // ==> 12
        int d=nums.removeFirst();    // ==> 11
        int e=nums.removeFirst(); //     ==> 9
        int f=nums.removeLast(); //      ==> 13
        int g=nums.removeFirst(); //     ==> 8
        int h=nums.removeFirst();  //   ==> 5
        int i=nums.removeLast();  //should be 10    ==> null
        System.out.println();
        System.out.println('W');
    }

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        gradescopeTest();
    }
}