package com.zhd.shj.entity;

import java.math.BigDecimal;
import java.util.List;

public class RectangleCalculator {
    double x1,y1,x2,y2,w;

   public RectangleCalculator(double x1, double y1, double x2, double y2, double w)
   {
       this.x1=x1;
       this.x2=x2;
       this.y1=y1; this.y2=y2;this.w=w;

       calculateVertices();
   }
    public static double[][] vertices;
    public  double[][] calculateVertices() {
        vertices = new double[4][2];
        double centerX = (x1 + x2) / 2.0;
        double centerY = (y1 + y2) / 2.0;
        double d = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double vx = x2 - x1;
        double vy = y2 - y1;
        double len = Math.sqrt(vx * vx + vy * vy);
        double ux = vx / len;
        double uy = vy / len;
        double halfW = w / 2.0;
        vertices[0][0] = x1 + halfW * uy;
        vertices[0][1] = y1 - halfW * ux;
        vertices[1][0] = x2 + halfW * uy;
        vertices[1][1] = y2 - halfW * ux;
        vertices[2][0] = x1 - halfW * uy;
        vertices[2][1] = y1 + halfW * ux;
        vertices[3][0] = x2 - halfW * uy;
        vertices[3][1] = y2 + halfW * ux;
        return vertices;
    }


}