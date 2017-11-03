package ru.justagod.justacore.helper;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by JustAGod on 02.11.17.
 */
public class Vector {

    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Vector vector) {
        return sqrt(pow(vector.x - x, 2) + pow(vector.y - y, 2));
    }

    public Vector subtract(Vector vector) {
        return new Vector(x - vector.x, y - vector.y);
    }

    public Vector add(Vector vector) {
        return new Vector(x + vector.x, y + vector.y);
    }

    public double length() {
        return sqrt(pow(x, 2) + pow(y, 2));
    }

    public Vector normalize() {
        return new Vector(x / length(), y / length());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector multiply(double speed) {
        return new Vector(x * speed, y * speed);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
