package ru.justagod.justacore.helper;

/**
 * Created by JustAGod on 03.11.17.
 */
public class Rect {

    private Vector upleft;
    private Vector downright;

    public Rect(double x1, double y1, double x2, double y2) {
        this(new Vector(x1, y1), new Vector(x2, y2));
    }

    public Rect(Vector uprigth, Vector downright) {
        this.upleft = uprigth;
        this.downright = downright;
    }

    public boolean isVectorInBounds(Vector vector) {
        return vector.getX() <= downright.getX() && vector.getX() >= upleft.getX() && vector.getY() <= downright.getY() && vector.getY() >= upleft.getY();
    }
}
