public class NBody {
    public static void main(String[]args){
      double T=Double.parseDouble(args[0]);
      double dt=Double.parseDouble(args[1]);
      String filename=args[2];
      double uniradius=NBody.readRadius(filename);
      Planet[] solar=NBody.readPlanets(filename);
      StdDraw.setScale(-uniradius,uniradius);
      /*
      double[] Xforces=new double[solar.length];
      double[] Yforces=new double[solar.length];
      */
      Double[] xForces = new Double[solar.length];
      Double[] yForces = new Double[solar.length];
      double time=0;
      int i=0;
      while(time<T){
        /*for (int i=0;i<solar.length;i++){
          double forcex=solar[i].calcNetForceExertedByX(solar);
          Xforces[i]=forcex;
          double forcey=solar[i].calcNetForceExertedByY(solar);
          Yforces[i]=forcey;
        }*/
        while (i < solar.length) {
          xForces[i]= solar[i].calcNetForceExertedByX(solar);
          yForces[i]= solar[i].calcNetForceExertedByY(solar);
          i += 1;
          }
        i=0;
        /*for (int i=0;i<solar.length;i++){
          solar[i].update(dt,Xforces[i],Yforces[i]);
        }*/
        while (i < solar.length) {
           solar[i].update(dt, xForces[i], yForces[i]);
           i += 1;
         }
        i = 0;
        StdDraw.picture(0,0,"./images/starfield.jpg");
        /*for (Planet aurora:solar){
            aurora.draw();
        }*/
        while (i < solar.length) {
              solar[i].draw();
              i += 1;
              }
        i = 0;
        StdDraw.show(10);
        time+=dt;
      }
      StdOut.printf("%d\n", solar.length);
      StdOut.printf("%.2e\n", uniradius);
      for (i = 0; i < solar.length; i++) {
	          StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   	      	solar[i].xxPos, solar[i].yyPos, solar[i].xxVel, solar[i].yyVel, solar[i].mass, solar[i].imgFileName);
      }
      }

    public static double readRadius(String directory){
      In in=new In(directory);
      int index=0;
      double radius=0;
      int numberofplanets=in.readInt();
      radius=in.readDouble();
      return radius;
    }

    public static Planet[] readPlanets(String directory){
      In in=new In(directory);
      int index=0;
      Planet[] solar=new Planet[5];
      int denero=in.readInt();
      double hug=in.readDouble();
      while (index < 5) {
         double xP = in.readDouble();
         double yP = in.readDouble();
         double xV = in.readDouble();
         double yV = in.readDouble();
         double m = in.readDouble();
         String img = in.readString();
         solar[index] = new Planet(xP, yP, xV, yV, m, img);
         index = index + 1;
      }
      /*for(int i=0; i<5;i++){
           double a=in.readDouble();
           double b=in.readDouble();
           double c=in.readDouble();
           double d=in.readDouble();
           double e=in.readDouble();
           String f=in.readString();
           Planet livestar=new Planet(a,b,c,d,e,f);
           solar[i]=livestar;
      }*/
      return solar;
    }
}
