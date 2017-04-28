import lab14lib.Generator;

/**
 * Created by vip on 4/27/17.
 */
public class StrangeBitwiseGenerator implements Generator {
    int state;
    int period;

    public StrangeBitwiseGenerator(int period1) {
        period = period1;
        state = 0;
    }

    public double next() {
        state += 1;
        int weirdState = state & (state >> 7) % period;
        return normalize(weirdState);
    }

    public double normalize(int x) {
        return (double) x * 2 / period - 1;
    }
}
