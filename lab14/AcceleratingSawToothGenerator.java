import lab14lib.Generator;

/**
 * Created by vip on 4/27/17.
 */
public class AcceleratingSawToothGenerator implements Generator {
    double factor;
    int period;
    int state;

    public AcceleratingSawToothGenerator(int period1, double factor1){
        period=period1;
        factor=factor1;
    }

    public double next(){
        state+=1;
        state=state%period;
        if(state==0){
            period=(int)(period*factor);
        }
        return normalize(state,period);
    }

    public double normalize(int x,int period){
        return (double) x * 2 / period - 1;
    }
}
