import static org.junit.Assert.*;

import org.junit.Test;

public class TestArrayDeque1B {
    @Test
    public void testAD() {
        StudentArrayDeque<Integer> darkside = new StudentArrayDeque<Integer>();
        ArrayDequeSolution<Integer> lightside = new ArrayDequeSolution<Integer>();
        OperationSequence printme = new OperationSequence();
        for (int i = 0; i < 100; i++) {
            int random = StdRandom.uniform(1, 1001);
            darkside.addFirst(random);
            lightside.addFirst(random);
            DequeOperation op = new DequeOperation("addFirst", random);
            printme.addOperation(op);
            darkside.addLast(random);
            lightside.addLast(random);
            DequeOperation op1 = new DequeOperation("addLast", random);
            printme.addOperation(op1);
        }
        for (int i = 0; i < 100; i++) {
            DequeOperation op = new DequeOperation("removeFirst");
            printme.addOperation(op);
            assertEquals(printme.toString(), lightside.removeFirst(), darkside.removeFirst());
            DequeOperation op1 = new DequeOperation("removeLast");
            printme.addOperation(op1);
            assertEquals(printme.toString(), lightside.removeLast(), darkside.removeLast());

        }
    }
}
