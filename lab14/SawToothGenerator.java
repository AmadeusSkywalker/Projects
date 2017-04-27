import lab14lib.Generator;

/**
 * Created by vip on 4/27/17.
 */
public class SawToothGenerator implements Generator {
    int state;
    int period;

    public SawToothGenerator(int period1) {
        period = period1;
        state = 0;
    }

    public double next() {
        state += 1;
        state = state % period;
        return normalize(state);
    }

    public double normalize(int x) {
        return (double) x * 2 / period - 1;
    }
}
