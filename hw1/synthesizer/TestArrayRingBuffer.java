package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<String>(4);
        arb.enqueue("may");
        arb.enqueue("the");
        arb.enqueue("force");
        arb.enqueue("be");
        assertEquals(4,arb.fillCount());
        assertEquals("may",arb.dequeue());
        assertEquals(3,arb.fillCount());
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 