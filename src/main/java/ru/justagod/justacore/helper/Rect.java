package ru.justagod.justacore.helper;

/**
 * Created by JustAGod on 03.11.17.
 */
public class Rect {

   private double x1;
   private double y1;
   private double x2;
   private double y2;

    public Rect(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean isVectorInBounds(Vector vector) {
        return vector.getX() <= x2 && vector.getX() >= x1 && vector.getY() <= y2 && vector.getY() >= y1;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }
}
