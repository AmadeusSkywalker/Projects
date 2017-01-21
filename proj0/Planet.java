public class Planet {
  double xxPos;
  double yyPos;
  double xxVel;
  double yyVel;
  double mass;
  String imgFileName;
  static double G=6.67*Math.pow(10,-11);
  public Planet(double xP, double yP, double xV,double yV, double m, String img){
    xxPos=xP;
    yyPos=yP;
    xxVel=xV;
    yyVel=yV;
    mass=m;
    imgFileName=img;
  }

  public static double bigminussmall(double x,double y){
    if (x>y){
      return x-y;
    }
    else{
      return y-x;
    }
  }

  public Planet(Planet p){
        this(p.xxPos,p.yyPos,p.xxVel,p.yyVel,p.mass,p.imgFileName);
  }

  public double calcDistance(Planet b){
         return Math.sqrt((this.xxPos-b.xxPos)*(this.xxPos-b.xxPos)+(this.yyPos-b.yyPos)*(this.yyPos-b.yyPos));
  }

  public double calcForceExertedBy(Planet b){
         return (G*this.mass*b.mass)/(Math.pow(this.calcDistance(b),2));
  }

  public double calcForceExertedByX(Planet b){
         return this.calcForceExertedBy(b)*bigminussmall(this.xxPos,b.xxPos)/this.calcDistance(b);
  }

  public double calcForceExertedByY(Planet b){
         return this.calcForceExertedBy(b)*bigminussmall(this.yyPos,b.yyPos)/this.calcDistance(b);
  }

}
