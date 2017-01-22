public class NBody {
    public static double readRadius(String directory){
      In in=new In(directory);
      int index=0;
      double radius=0;
      while (index<2){
        radius=in.readDouble();
        index=index+1;
      }
      return radius;
    }

    public static Planet[] readPlanets(String directory){
      In in=new In(directory);
      int index=0;
      Planet[] a=new Planet[5];
      while (index<2){
        nonsense=in.readDouble();
        index=index+1;
      }
      while(!in.isEmpty()){
           double a=in.readDouble();
           double b=in.readDouble();
           double c=in.readDouble();
           double d=in.readDouble();
           double e=in.readDouble();
           String f=in.readString();
           Planet livestar=Planet(a,b,c,d,e,f);
           a.add(livestar);
      }
      return a;
    }
}
