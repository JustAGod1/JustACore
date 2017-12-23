package ru.justagod.justacore.gui.model;

/**
 * Created by JustAGod on 04.11.17.
 */
public class Dimensions {

    private double width;
    private double height;

    public Dimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void appendVector(Vector vector) {
        width += vector.getX();
        height += vector.getY();
    }

    public void appendDimensions(Dimensions dimensions) {
        width += dimensions.width;
        height += dimensions.height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Dimensions{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
