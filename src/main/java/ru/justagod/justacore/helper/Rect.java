package ru.justagod.justacore.helper;

/**
 * Created by JustAGod on 03.11.17.
 */
public class Rect {

    private Vector uprigth;
    private Vector downleft;

    public Rect(double x1, double y1, double x2, double y2) {
        this(new Vector(x1, y1), new Vector(x2, y2));
    }

    public Rect(Vector uprigth, Vector downleft) {
        this.uprigth = uprigth;
        this.downleft = downleft;
    }

    public boolean isVectorInBounds(Vector vector) {
        return vector.subtract(uprigth).length() <= downleft.subtract(uprigth).length() && downleft.subtract(vector).length() <= downleft.subtract(uprigth).length();
    }
}
