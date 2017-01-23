public class NBody {
    public static void main(String[]args){
      double T=Double.parseDouble(args[0]);
      double dt=Double.parseDouble(args[1]);
      String filename=args[2];
      double uniradius=NBody.readRadius(filename);
      Planet[] solar=NBody.readPlanets(filename);
      StdDraw.setScale(-uniradius,uniradius);
      StdDraw.picture(0,0,"./images/starfield.jpg");
      for (Planet aurora:solar){
          aurora.draw();
      }
      double time=0;
      while(time<T){
        double[] Xforces=new double[5];
        double[] Yforces=new double[5];
        for (int i=0;i<5;i++){
          double forcex=solar[i].calcNetForceExertedByX(solar);
          Xforces[i]=forcex;
          double forcey=solar[i].calcNetForceExertedByY(solar);
          Yforces[i]=forcey;
        }
        for (int i=0;i<5;i++){
          solar[i].update(dt,Xforces[i],Yforces[i]);
        }
        StdDraw.picture(0,0,"./images/starfield.jpg");
        for (Planet aurora:solar){
            aurora.draw();
        }
        StdDraw.show(10);
        time=time+dt;
      }
      StdOut.printf("%d\n", solar.length);
      StdOut.printf("%.2e\n", uniradius);
      for (int i = 0; i < solar.length; i++) {
	          StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   	      	solar[i].xxPos, solar[i].yyPos, solar[i].xxVel, solar[i].yyVel, solar[i].mass, solar[i].imgFileName);
      }
      }

    public static double readRadius(String directory){
      In in=new In(directory);
      int index=0;
      double radius=0;
      int papajones=in.readInt();
      radius=in.readDouble();
      return radius;
    }

    public static Planet[] readPlanets(String directory){
      In in=new In(directory);
      int index=0;
      Planet[] solar=new Planet[5];
      int denero=0;
      double hug=0;
      denero=in.readInt();
      hug=in.readDouble();
      for(int i=0; i<5;i++){
           double a=in.readDouble();
           double b=in.readDouble();
           double c=in.readDouble();
           double d=in.readDouble();
           double e=in.readDouble();
           String f=in.readString();
           Planet livestar=new Planet(a,b,c,d,e,f);
           solar[i]=livestar;
      }
      return solar;
    }
}
