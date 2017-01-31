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

    public static void addremoveTest(){
        boolean passed=true;
        System.out.println("Running add tests.\n");
        ArrayDeque<String> army=new ArrayDeque<>();
        army.addFirst("soldiers");
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

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        addremoveTest();
    }
}